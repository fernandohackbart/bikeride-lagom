package com.bikeride.utils.servicelocator.dns

import java.net.URI

import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import com.lightbend.lagom.scaladsl.api.Descriptor
import com.lightbend.lagom.internal.client.CircuitBreakers
import com.lightbend.lagom.scaladsl.client.CircuitBreakingServiceLocator
import scala.concurrent.{ExecutionContext, Future}

class DNSServiceLocator(
                         serviceLocatorService: ActorRef,
                         system: ActorSystem,
                         circuitBreakers: CircuitBreakers,
                         implicit val ec: ExecutionContext) extends CircuitBreakingServiceLocator(circuitBreakers) {

  val settings = Settings(system)

  override def locate(name: String, serviceCall: Descriptor.Call[_, _]): Future[Option[URI]] =
    serviceLocatorService
      .ask(ServiceLocator.GetAddress(name))(settings.resolveTimeout1 + settings.resolveTimeout1 + settings.resolveTimeout2)
      .mapTo[ServiceLocator.Addresses]
      .map {
        case ServiceLocator.Addresses(addresses) =>
          addresses
            .headOption
            .map(sa => new URI(sa.protocol, null, sa.host, sa.port, null, null, null))
      }
}