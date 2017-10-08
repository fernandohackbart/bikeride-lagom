package com.bikeride.utils.servicelocator.dns

import akka.actor.{Actor, ExtendedActorSystem, Extension, ExtensionKey}

import scala.concurrent.duration.{Duration, FiniteDuration, MILLISECONDS}
import com.typesafe.config.Config
import scala.util.matching.Regex
import scala.collection.JavaConversions._

object Settings extends ExtensionKey[Settings]

class Settings(system: ExtendedActorSystem) extends Extension {
  val nameTranslators: Seq[(Regex, String)] =
    serviceLocatorDns
      .getObjectList("name-translators")
      .toList
      .flatMap(_.toMap.map {
        case (k, v) => k.r -> v.unwrapped().toString
      })

  val srvTranslators: Seq[(Regex, String)] =
    serviceLocatorDns
      .getObjectList("srv-translators")
      .toList
      .flatMap(_.toMap.map {
        case (k, v) => k.r -> v.unwrapped().toString
      })

  val resolveTimeout1: FiniteDuration =
    duration(serviceLocatorDns, "resolve-timeout1")

  val resolveTimeout2: FiniteDuration =
    duration(serviceLocatorDns, "resolve-timeout2")

  private lazy val config = system.settings.config
  private lazy val serviceLocatorDns = config.getConfig("service-locator-dns")

  private def duration(config: Config, key: String): FiniteDuration =
    Duration(config.getDuration(key, MILLISECONDS), MILLISECONDS)
}

trait ActorSettings {
  this: Actor =>

  protected val settings: Settings =
    Settings(context.system)
}
