package com.bikeride.ride.impl

import java.time.Instant
import java.util.UUID

import com.bikeride.biker.api.BikerID
import com.bikeride.track.api.TrackID
import play.api.libs.json._

case class RideState (id: UUID,
                      name: String,
                      organizer: BikerID,
                      limit: Int,
                      track: TrackID,
                      open: Boolean,
                      started: Boolean,
                      suspended: Boolean,
                      finished: Boolean,
                      leaders: Seq[BikerID],
                      riders: Seq[BikerID],
                      opentime: Option[Instant] = None,
                      closetime: Option[Instant] = None,
                      starttime: Option[Instant] = None,
                      finishtime: Option[Instant] = None)
object RideState {
  implicit val format: Format[RideState] = Json.format
}

case class RideChange (id: UUID,
                       name:  Option[String] = None,
                       organizer: Option[BikerID] = None,
                       limit: Option[Int] = None,
                       track: Option[TrackID] = None)
object RideChange {
  implicit val format: Format[RideChange] = Json.format
}