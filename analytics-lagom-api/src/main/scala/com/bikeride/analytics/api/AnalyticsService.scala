package com.bikeride.analytics.api

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import com.lightbend.lagom.scaladsl.api.transport.Method

trait AnalyticsService extends Service {

  def getStatistics: ServiceCall[NotUsed, AnalyticsAccrual]

  override final def descriptor = {
    import Service._
    named("analytics")
      .withCalls(
        restCall(Method.GET,"/api/analytics/statistics", getStatistics)
      ).withAutoAcl(true)
  }
}