package com.bikeride.analytics.api

import play.api.libs.json.{Format, Json}

case class AnalyticsAccrual(event: String,
                             count : String)
object  AnalyticsAccrual {
  implicit val format: Format[AnalyticsAccrual] = Json.format
}