package com.bikeride.authentication.impl


import com.bikeride.authentication.api.AuthenticationService
import com.bikeride.biker.api.BikerService
import com.bikeride.utils.servicelocator.dns.DNSServiceLocatorComponents
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.server._
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import play.api.libs.ws.ahc.AhcWSComponents
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.softwaremill.macwire._

class AuthenticationLoader extends LagomApplicationLoader{

  override def load(context: LagomApplicationContext) =
    new AuthenticationApplication(context) with DNSServiceLocatorComponents

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new AuthenticationApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[AuthenticationService])
}

abstract class AuthenticationApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with CassandraPersistenceComponents
    with LagomKafkaComponents
    with AhcWSComponents {

  override lazy val lagomServer = serverFor[AuthenticationService](wire[AuthenticationServiceImpl])
  override lazy val jsonSerializerRegistry = AuthenticationSerializerRegistry

  lazy val bikerService = serviceClient.implement[BikerService]

  persistentEntityRegistry.register(wire[AuthenticationEntity])

}
