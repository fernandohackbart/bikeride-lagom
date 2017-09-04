package com.bikeride.biker.impl

import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}

object BikerSerializerRegistry extends JsonSerializerRegistry {
  override def serializers = List(
    JsonSerializer[BikerState],
    JsonSerializer[BikerCreated],
    JsonSerializer[BikerNameChanged],
    JsonSerializer[BikerAvatarB64Changed],
    JsonSerializer[BikerBloodTypeChanged],
    JsonSerializer[BikerMobileChanged],
    JsonSerializer[BikerEmailChanged],
    JsonSerializer[BikerDeactivated],
    JsonSerializer[CreateBiker],
    JsonSerializer[ChangeBikerName],
    JsonSerializer[ChangeBikerAvatarB64],
    JsonSerializer[ChangeBikerBloodType],
    JsonSerializer[ChangeBikerMobile],
    JsonSerializer[ChangeBikerEmail],
    JsonSerializer[ActivateBiker],
    JsonSerializer[DeactivateBiker],
    JsonSerializer[GetBiker.type]
  )
}




