package com.bikeride.utils.servicelocator.dns

import java.util.concurrent.ThreadLocalRandom

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.io.AsyncDnsResolver.SrvResolved
import akka.io.{Dns, IO}
import akka.pattern.{AskTimeoutException, ask, pipe}
import ru.smslv.akka.dns.raw.SRVRecord

import scala.annotation.tailrec
import scala.collection.immutable
import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration
import scala.util.control.NonFatal
import scala.util.matching.Regex

object ServiceLocator {

  val Name = "DNSServiceLocatorSimple"

  def props: Props =
    Props(new ServiceLocator)

  final case class GetAddress(name: String)

  final case class GetAddresses(name: String)

  sealed abstract case class Addresses(addresses: Seq[ServiceAddress])
  object Addresses {
    private val empty: Addresses = new Addresses(Nil) {}

    def apply(addresses: Seq[ServiceAddress]): Addresses =
      if (addresses.nonEmpty) new Addresses(addresses) {}
      else empty
  }

  final case class ServiceAddress(protocol: String, hostname: String, host: String, port: Int)

  private[dns] final case class RequestContext(replyTo: ActorRef, resolveOne: Boolean, srv: Seq[SRVRecord])

  private[dns] final case class ReplyContext(resolutions: Seq[(Dns.Resolved, SRVRecord)], rc: RequestContext)

  @tailrec
  private[dns] def matchTranslation(name: String, translators: Seq[(Regex, String)]): Option[String] =
    translators match {
      case (r, s) +: tail =>
        val matcher = r.pattern.matcher(name)
        if (matcher.matches())
          Some(matcher.replaceAll(s))
        else
          matchTranslation(name, tail)
      case _ => None
    }

  private[dns] def protocolFromName(name: String): String =
    name.iterator.dropWhile(_ != '.').drop(1).takeWhile(_ != '.').drop(1).mkString
}

class ServiceLocator extends Actor with ActorSettings with ActorLogging {

  import ServiceLocator._

  private val _dns = IO(Dns)(context.system)
  protected def dns: ActorRef = _dns

  override def receive: Receive = {
    case GetAddress(name) =>
//      log.debug("###################### receive - Resolving: {}", name)
      resolveSrv(name, resolveOne = true)

    case GetAddresses(name) =>
//      log.debug("###################### receive - Resolving many: {}", name)
      resolveSrv(name, resolveOne = false)

    case rc: RequestContext =>
      // When we return just one address then we randomize which of the candidates to return
//      log.debug("###################### receive - RequestContext: {}", rc)
      val (srvFrom, srvSize) =
        if (rc.resolveOne && rc.srv.nonEmpty)
          (ThreadLocalRandom.current.nextInt(rc.srv.size), 1)
        else
          (0, rc.srv.size)
      import context.dispatcher
      val resolutions =
        rc.srv
          .slice(srvFrom, srvFrom + srvSize)
          .map { srv =>
            resolveDns(srv.target).map(_ -> srv)
          }
      Future
        .sequence(resolutions)
        .map(ReplyContext(_, rc))
        .pipeTo(self)

    case ReplyContext(resolutions, rc) =>
      log.debug("###################### receive - Resolved: resolutions: {} ", resolutions)
      val addresses =
        resolutions
          .flatMap {
            case (resolved, srv) => {
              val protocol = protocolFromName(srv.name)
              val port = srv.port
              log.debug("###################### receive - Resolved: protocol:{} port:{} target:{}", protocol,port,srv.target)
              resolved.ipv4.map(host => ServiceAddress(protocol, srv.target, host.getHostAddress, port)) ++
                resolved.ipv6.map(host => ServiceAddress(protocol, srv.target, host.getHostAddress, port))

            }
          }
      log.debug("###################### receive - Resolved: addresses:{} ",addresses)
      rc.replyTo ! Addresses(addresses)
  }

  private def resolveSrv(name: String, resolveOne: Boolean): Unit = {
//    log.debug("###################### resolveSrv - Resolving: {}", name)
    val matchedName = matchTranslation(name, settings.nameTranslators)
    matchedName.foreach { mn =>
      if (name != mn)
        log.debug("###################### resolveSrv - Translated {} to {}", name, mn)

      val replyTo = sender()
      import context.dispatcher
//      log.debug("###################### resolveSrv - resolveSrvOnce {} ", mn)
      resolveSrvOnce(mn, settings.resolveTimeout1)
        .recoverWith {
          case _: AskTimeoutException =>
            resolveSrvOnce(mn, settings.resolveTimeout1)
              .recoverWith {
                case _: AskTimeoutException =>
                  resolveSrvOnce(mn, settings.resolveTimeout2)
              }
        }
        .recover {
          case iobe: IndexOutOfBoundsException =>
            log.error("###################### resolveSrv - Could not substitute the service name with the name translator {}", iobe.getMessage)
            SrvResolved(mn, Nil)

          case ate: AskTimeoutException =>
            log.debug("###################### resolveSrv - Timed out querying DNS SRV for {}", name)
            SrvResolved(mn, Nil)

          case NonFatal(e) =>
            log.error(e, "###################### resolveSrv - Unexpected error when resolving an SRV record")
            SrvResolved(mn, Nil)
        }
        .map(resolved =>
          RequestContext(
            replyTo,
            resolveOne,
            resolved.srv.map { record =>
              matchTranslation(record.name, settings.srvTranslators) match {
                case Some(newName) if name != newName =>
                  log.debug("###################### resolveSrv - RequestContext response Translated {} to {}", record.name, newName)
                  record.copy(name = newName)
                case _ =>
                  log.debug("###################### resolveSrv - RequestContext NOT Translated {}", record.name)
                  record
              }
            }
          )
        )
        .pipeTo(self)
    }
    if (matchedName.isEmpty)
      log.debug("###################### resolveSrv - matchedName.isEmpty")
      sender() ! Addresses(Nil)
  }

  private def resolveSrvOnce(name: String, resolveTimeout: FiniteDuration): Future[SrvResolved] = {
    import context.dispatcher
//    log.debug("###################### resolveSrvOnce - resolving {}", name)
    dns
      .ask(Dns.Resolve(name))(resolveTimeout)
      .map {
        case srvResolved: SrvResolved => {
//          log.debug("###################### resolveSrvOnce - resolved {}", srvResolved)
          srvResolved
        }
        case _: Dns.Resolved          => {
//          log.debug("###################### resolveSrvOnce - NOT resolved")
          SrvResolved(name, Nil)
        }
      }
    //####################################################################################
    // HERE SOMETHING IS WRONG
  }

  private def resolveDns(name: String): Future[Dns.Resolved] = {
    import context.dispatcher
    dns
      .ask(Dns.Resolve(name))(settings.resolveTimeout1)
      .recoverWith {
        case _: AskTimeoutException =>
//          log.debug("###################### resolveDns - resolving {}", name)
          dns.ask(Dns.Resolve(name))(settings.resolveTimeout1)
            .recoverWith {
              case _: AskTimeoutException =>
//                log.debug("###################### resolveDns - timeout ")
                dns.ask(Dns.Resolve(name))(settings.resolveTimeout2)
            }
      }
      .mapTo[Dns.Resolved]
      .recover {
        case ate: AskTimeoutException =>
//          log.debug("###################### resolveDns - Timed out querying DNS for {}", name)
          Dns.Resolved(name, Nil)

        case NonFatal(e) =>
//          log.error(e, "###################### resolveDns - Unexpected error when resolving an DNS record")
          Dns.Resolved(name, Nil)
      }
  }
}
