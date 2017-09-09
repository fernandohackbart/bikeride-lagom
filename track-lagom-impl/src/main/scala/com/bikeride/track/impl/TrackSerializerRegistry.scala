package com.bikeride.track.impl

import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}

object TrackSerializerRegistry extends JsonSerializerRegistry {
  override def serializers = List(
    JsonSerializer[TrackState],
    JsonSerializer[TrackCreated],
    JsonSerializer[TrackNameChanged],
    JsonSerializer[TrackMaintainerChanged],
    JsonSerializer[TrackActivated],
    JsonSerializer[TrackDeactivated],
    JsonSerializer[TrackWayPointAdded],
    JsonSerializer[TrackWayPointRemoved],
    JsonSerializer[TrackInitialWayPointMarked],
    JsonSerializer[CreateTrack],
    JsonSerializer[ChangeTrackName],
    JsonSerializer[ChangeTrackMaintainer],
    JsonSerializer[ActivateTrack],
    JsonSerializer[DeactivateTrack],
    JsonSerializer[AddTrackWayPoint],
    JsonSerializer[RemoveTrackWayPoint],
    JsonSerializer[MarkWayPointInitial],
    JsonSerializer[GetTrack.type]
  )
}