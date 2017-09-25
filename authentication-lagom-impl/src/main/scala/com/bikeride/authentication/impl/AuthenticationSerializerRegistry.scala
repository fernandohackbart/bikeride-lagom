package com.bikeride.authentication.impl

import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}

object AuthenticationSerializerRegistry extends JsonSerializerRegistry {
  override def serializers = List(
    JsonSerializer[AuthenticationPINState],
    JsonSerializer[AuthenticationPINGenerated],
    JsonSerializer[GeneratePIN],
    JsonSerializer[GetPIN.type]
  )
}