package com.bikeride.track.impl

import com.bikeride.track.api.TrackService
import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.server._
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import play.api.libs.ws.ahc.AhcWSComponents
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.softwaremill.macwire._

class TrackLoader extends LagomApplicationLoader{

  override def load(context: LagomApplicationContext): LagomApplication =
    new TrackApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new TrackApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[TrackService])
}

abstract class TrackApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with CassandraPersistenceComponents
    with LagomKafkaComponents
    with AhcWSComponents {

  override lazy val lagomServer = serverFor[TrackService](wire[TrackServiceImpl])
  override lazy val jsonSerializerRegistry = TrackSerializerRegistry

  lazy val trackrService = serviceClient.implement[TrackService]

  persistentEntityRegistry.register(wire[TrackEntity])
  readSide.register(wire[TrackEventProcessor])

}