package com.bikeride.analytics.impl

import com.lightbend.lagom.scaladsl.playjson.JsonSerializerRegistry

object AnalyticsSerializerRegistry extends JsonSerializerRegistry {
  override def serializers = List(
  )
}