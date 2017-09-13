package com.bikeride.track.impl

import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventShards, AggregateEventTag}
import play.api.libs.json.{Format, Json}

object TrackEvent {
  val NumShards = 3
  val Tags = AggregateEventTag.sharded[TrackEvent](NumShards)
}

sealed trait TrackEvent extends AggregateEvent[TrackEvent]
{
  override def aggregateTag: AggregateEventShards[TrackEvent] = TrackEvent.Tags
}

case class TrackCreated(track: TrackState) extends TrackEvent
object TrackCreated {
  implicit val format: Format[TrackCreated] = Json.format
}

case class TrackNameChanged(track: TrackState) extends TrackEvent
object TrackNameChanged {
  implicit val format: Format[TrackNameChanged] = Json.format
}

case class TrackMaintainerChanged(track: TrackState) extends TrackEvent
object TrackMaintainerChanged {
  implicit val format: Format[TrackMaintainerChanged] = Json.format
}

case class TrackActivated(track: TrackState) extends TrackEvent
object TrackActivated {
  implicit val format: Format[TrackActivated] = Json.format
}

case class TrackDeactivated(track: TrackState) extends TrackEvent
object TrackDeactivated {
  implicit val format: Format[TrackDeactivated] = Json.format
}

case class TrackWayPointAdded(track: TrackState) extends TrackEvent
object TrackWayPointAdded {
  implicit val format: Format[TrackWayPointAdded] = Json.format
}

case class TrackWayPointRemoved(track: TrackState) extends TrackEvent
object TrackWayPointRemoved {
  implicit val format: Format[TrackWayPointRemoved] = Json.format
}

case class TrackInitialWayPointMarked(track: TrackState) extends TrackEvent
object TrackInitialWayPointMarked {
  implicit val format: Format[TrackInitialWayPointMarked] = Json.format
}

