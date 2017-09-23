package com.bikeride.ride.impl


import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventShards, AggregateEventTag}
import play.api.libs.json.{Format, Json}

object RideEvent {
  val NumShards = 3
  val Tags = AggregateEventTag.sharded[RideEvent](NumShards)
}

sealed trait RideEvent extends AggregateEvent[RideEvent]
{
  override def aggregateTag: AggregateEventShards[RideEvent] = RideEvent.Tags
}

case class RideCreated(track: RideState) extends RideEvent
object RideCreated {
  implicit val format: Format[RideCreated] = Json.format
}

case class RideNameChanged(track: RideState) extends RideEvent
object RideNameChanged {
  implicit val format: Format[RideNameChanged] = Json.format
}

case class RideOrganizerChanged(track: RideState) extends RideEvent
object RideOrganizerChanged {
  implicit val format: Format[RideOrganizerChanged] = Json.format
}

case class RideTrackChanged(track: RideState) extends RideEvent
object RideTrackChanged {
  implicit val format: Format[RideTrackChanged] = Json.format
}

case class RideLimitChanged(track: RideState) extends RideEvent
object RideLimitChanged {
  implicit val format: Format[RideLimitChanged] = Json.format
}

case class RideLeadersChanged(track: RideState) extends RideEvent
object RideLeadersChanged {
  implicit val format: Format[RideLeadersChanged] = Json.format
}

case class RideSubscriptionsOpened(track: RideState) extends RideEvent
object RideSubscriptionsOpened {
  implicit val format: Format[RideSubscriptionsOpened] = Json.format
}

case class RideSubscriptionsClosed(track: RideState) extends RideEvent
object RideSubscriptionsClosed {
  implicit val format: Format[RideSubscriptionsClosed] = Json.format
}

case class RideRiderSubscribed(track: RideState) extends RideEvent
object RideRiderSubscribed {
  implicit val format: Format[RideRiderSubscribed] = Json.format
}

case class RideRiderUnsubscribed(track: RideState) extends RideEvent
object RideRiderUnsubscribed {
  implicit val format: Format[RideRiderUnsubscribed] = Json.format
}

case class RideStarted(track: RideState) extends RideEvent
object RideStarted {
  implicit val format: Format[RideStarted] = Json.format
}

case class RideSuspended(track: RideState) extends RideEvent
object RideSuspended {
  implicit val format: Format[RideSuspended] = Json.format
}

case class RideResumed(track: RideState) extends RideEvent
object RideResumed {
  implicit val format: Format[RideResumed] = Json.format
}

case class RideFinished(track: RideState) extends RideEvent
object RideFinished {
  implicit val format: Format[RideFinished] = Json.format
}
