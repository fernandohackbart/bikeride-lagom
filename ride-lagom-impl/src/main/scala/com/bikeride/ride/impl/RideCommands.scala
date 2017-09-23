package com.bikeride.ride.impl

import akka.Done
import com.bikeride.biker.api.BikerID
import com.bikeride.ride.api.RideID
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import play.api.libs.json.{Format, Json}
import com.bikeride.utils.json._

sealed trait RideCommand

case class CreateRide(ride: RideState) extends RideCommand with ReplyType[Done]
object CreateRide {
  implicit val format: Format[CreateRide] = Json.format
}

case class ChangeRideName(rideChange: RideChange) extends RideCommand with ReplyType[Done]
object ChangeRideName {
  implicit val format: Format[ChangeRideName] = Json.format
}

case class ChangeRideOrganizer(rideChange: RideChange) extends RideCommand with ReplyType[Done]
object ChangeRideOrganizer {
  implicit val format: Format[ChangeRideOrganizer] = Json.format
}

case class ChangeRideTrack(rideChange: RideChange) extends RideCommand with ReplyType[Done]
object ChangeRideTrack {
  implicit val format: Format[ChangeRideTrack] = Json.format
}

case class ChangeRideLimit(rideChange: RideChange) extends RideCommand with ReplyType[Done]
object ChangeRideLimit {
  implicit val format: Format[ChangeRideLimit] = Json.format
}

//TODO: who can be a leader? Biker attributes? Qualifications?
case class AddRideLeader(rideID: RideID, leaderID: BikerID) extends RideCommand with ReplyType[Done]
object AddRideLeader {
  implicit val format: Format[AddRideLeader] = Json.format
}

case class RemoveRideLeader(rideID: RideID, leaderID: BikerID) extends RideCommand with ReplyType[Done]
object RemoveRideLeader {
  implicit val format: Format[RemoveRideLeader] = Json.format
}

case class OpenSubscriptionsToRide(rideID: RideID) extends RideCommand with ReplyType[Done]
object OpenSubscriptionsToRide {
  implicit val format: Format[OpenSubscriptionsToRide] = Json.format
}

case class CloseSubscriptionsToRide(rideID: RideID) extends RideCommand with ReplyType[Done]
object CloseSubscriptionsToRide {
  implicit val format: Format[CloseSubscriptionsToRide] = Json.format
}

case class SubscribeRideRider(rideID: RideID, riderID: BikerID) extends RideCommand with ReplyType[Done]
object SubscribeRideRider {
  implicit val format: Format[SubscribeRideRider] = Json.format
}

case class UnsubscribeRideRider(rideID: RideID, riderID: BikerID) extends RideCommand with ReplyType[Done]
object UnsubscribeRideRider {
  implicit val format: Format[UnsubscribeRideRider] = Json.format
}

case class StartRide(rideID: RideID) extends RideCommand with ReplyType[Done]
object StartRide {
  implicit val format: Format[StartRide] = Json.format
}

case class SuspendRide(rideID: RideID) extends RideCommand with ReplyType[Done]
object SuspendRide {
  implicit val format: Format[SuspendRide] = Json.format
}

case class ResumeRide(rideID: RideID) extends RideCommand with ReplyType[Done]
object ResumeRide {
  implicit val format: Format[ResumeRide] = Json.format
}

case class FinishRide(rideID: RideID) extends RideCommand with ReplyType[Done]
object FinishRide {
  implicit val format: Format[FinishRide] = Json.format
}

case object GetRide extends RideCommand with ReplyType[Option[RideState]] {
  implicit val format: Format[GetRide.type] = JSONFormats.singletonFormat(GetRide)
}