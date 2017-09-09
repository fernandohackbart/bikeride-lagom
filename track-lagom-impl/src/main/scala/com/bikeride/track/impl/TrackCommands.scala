package com.bikeride.track.impl

import java.util.UUID
import akka.Done
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import play.api.libs.json.{Format, Json}

sealed trait TrackCommand

case class CreateTrack(track: TrackState) extends TrackCommand with ReplyType[Done]
object CreateTrack {
  implicit val format: Format[CreateTrack] = Json.format
}

case class ChangeTrackName(trackChange: TrackChange) extends TrackCommand with ReplyType[Done]
object ChangeTrackName {
  implicit val format: Format[ChangeTrackName] = Json.format
}

case class ChangeTrackMaintainer(trackChange: TrackChange) extends TrackCommand with ReplyType[Done]
object ChangeTrackMaintainer {
  implicit val format: Format[ChangeTrackMaintainer] = Json.format
}

case class ActivateTrack(trackID: UUID) extends TrackCommand with ReplyType[Done]
object ActivateTrack {
  implicit val format: Format[ActivateTrack] = Json.format
}

case class DeactivateTrack(trackID: UUID) extends TrackCommand with ReplyType[Done]
object DeactivateTrack {
  implicit val format: Format[DeactivateTrack] = Json.format
}

case class AddTrackWayPoint(trackID: UUID,waypoint: TrackWaypoint) extends TrackCommand with ReplyType[Done]
object AddTrackWayPoint {
  implicit val format: Format[AddTrackWayPoint] = Json.format
}

case class RemoveTrackWayPoint(trackID: UUID,waypointID: UUID) extends TrackCommand with ReplyType[Done]
object RemoveTrackWayPoint {
  implicit val format: Format[RemoveTrackWayPoint] = Json.format
}

case class MarkWayPointInitial(trackID: UUID,waypointID: UUID) extends TrackCommand with ReplyType[Done]
object MarkWayPointInitial {
  implicit val format: Format[MarkWayPointInitial] = Json.format
}

case object GetTrack extends TrackCommand with ReplyType[Option[TrackState]] {
  implicit val format: Format[GetTrack.type] = JsonFormats.singletonFormat(GetTrack)
}