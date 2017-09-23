package com.bikeride.ride.impl


import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}

object RideSerializerRegistry extends JsonSerializerRegistry {
  override def serializers = List(
    JsonSerializer[RideState],
    JsonSerializer[RideCreated],
    JsonSerializer[RideNameChanged],
    JsonSerializer[RideOrganizerChanged],
    JsonSerializer[RideTrackChanged],
    JsonSerializer[RideLimitChanged],
    JsonSerializer[RideLeadersChanged],
    JsonSerializer[RideSubscriptionsOpened],
    JsonSerializer[RideSubscriptionsClosed],
    JsonSerializer[RideRiderSubscribed],
    JsonSerializer[RideRiderUnsubscribed],
    JsonSerializer[RideStarted],
    JsonSerializer[RideSuspended],
    JsonSerializer[RideFinished],
    JsonSerializer[CreateRide],
    JsonSerializer[ChangeRideName],
    JsonSerializer[ChangeRideOrganizer],
    JsonSerializer[ChangeRideTrack],
    JsonSerializer[ChangeRideLimit],
    JsonSerializer[AddRideLeader],
    JsonSerializer[RemoveRideLeader],
    JsonSerializer[SubscribeRideRider],
    JsonSerializer[UnsubscribeRideRider],
    JsonSerializer[StartRide],
    JsonSerializer[SuspendRide],
    JsonSerializer[ResumeRide],
    JsonSerializer[FinishRide],
    JsonSerializer[GetRide.type]
  )
}