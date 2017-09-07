package cmo.bikeride.track.impl

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity

class BikerEntity extends PersistentEntity {
  override type Command = TrackCommand
  override type Event = TrackEvent
  override type State = Option[TrackState]

  override def initialState: Option[TrackState] = None
}
