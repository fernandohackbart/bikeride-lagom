package com.bikeride.track.impl

import java.util.UUID
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

case class TrackCreated(biker: TrackState) extends TrackEvent
object TrackCreated {
  implicit val format: Format[TrackCreated] = Json.format
}

case class TrackNameChanged(biker: TrackState) extends TrackEvent
object TrackNameChanged {
  implicit val format: Format[TrackNameChanged] = Json.format
}

case class TrackMaintainerChanged(biker: TrackState) extends TrackEvent
object TrackMaintainerChanged {
  implicit val format: Format[TrackMaintainerChanged] = Json.format
}

case class TrackActivated(biker: TrackState) extends TrackEvent
object TrackActivated {
  implicit val format: Format[TrackActivated] = Json.format
}

case class TrackDeactivated(biker: TrackState) extends TrackEvent
object TrackDeactivated {
  implicit val format: Format[TrackDeactivated] = Json.format
}

case class TrackWayPointAdded(biker: TrackState) extends TrackEvent
object TrackWayPointAdded {
  implicit val format: Format[TrackWayPointAdded] = Json.format
}

case class TrackWayPointRemoved(biker: TrackState) extends TrackEvent
object TrackWayPointRemoved {
  implicit val format: Format[TrackWayPointRemoved] = Json.format
}

case class TrackInitialWayPointMarked(biker: TrackState) extends TrackEvent
object TrackInitialWayPointMarked {
  implicit val format: Format[TrackInitialWayPointMarked] = Json.format
}

