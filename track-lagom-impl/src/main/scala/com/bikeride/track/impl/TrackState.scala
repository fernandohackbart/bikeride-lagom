package com.bikeride.track.impl

import java.util.UUID
import play.api.libs.json._

case class TrackState (id: UUID,
                       name: String,
                       maintainer: UUID,
                       waypoints: Option[Seq[TrackWaypoint]] = None,
                       active: Boolean)
object TrackState {
  implicit val format: Format[TrackState] = Json.format
}

case class TrackWaypoint(id: UUID, name: String, coordinates: String)
object  TrackWaypoint {
  implicit val format: Format[TrackWaypoint] = Json.format
}

case class TrackChange (id: UUID,
                        name:  Option[String] = None,
                        maintainer: Option[UUID] = None)
object TrackChange {
  implicit val format: Format[TrackChange] = Json.format
}