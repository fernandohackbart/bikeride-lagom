package com.bikeride.analytics.impl

import com.bikeride.authentication.api.AuthenticationService
import com.bikeride.analytics.api.AnalyticsService
import com.lightbend.lagom.scaladsl.dns.DnsServiceLocatorComponents
import com.lightbend.lagom.scaladsl.server._
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import play.api.libs.ws.ahc.AhcWSComponents
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.softwaremill.macwire._

class AnalyticsLoader extends LagomApplicationLoader{

  override def load(context: LagomApplicationContext) =
    new AnalyticsApplication(context) with DnsServiceLocatorComponents

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new AnalyticsApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[AnalyticsService])
}

abstract class AnalyticsApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with CassandraPersistenceComponents
    with LagomKafkaComponents
    with AhcWSComponents {

  override lazy val lagomServer = serverFor[AnalyticsService](wire[AnalyticsServiceImpl])
  override lazy val jsonSerializerRegistry = AnalyticsSerializerRegistry

  lazy val authnService = serviceClient.implement[AuthenticationService]

}