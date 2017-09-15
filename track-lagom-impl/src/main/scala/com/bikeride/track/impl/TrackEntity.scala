package com.bikeride.track.impl

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity
import org.slf4j.Logger

class TrackEntity extends PersistentEntity {
  override type Command = TrackCommand
  override type Event = TrackEvent
  override type State = Option[TrackState]

  override def initialState: Option[TrackState] = None

  //val log: Logger = Logger[TrackApplication]

  override def behavior: Behavior = {
    case Some(track)  => postAdded
    case None => initial
  }

  private val postAdded: Actions = {
    Actions().onReadOnlyCommand[GetTrack.type, Option[TrackState]] {
      case (GetTrack, ctx, state) => ctx.reply(state)
    }.onReadOnlyCommand[CreateTrack, Done] {
      case (CreateTrack(track), ctx, state) =>
        ctx.invalidCommand(s"Track with id $track.id already exists")
    }.onCommand[ChangeTrackName, Done] {
      case (ChangeTrackName(track), ctx, state) =>
        ctx.thenPersist(TrackNameChanged(TrackState(state.get.id, track.name.get, state.get.maintainer, state.get.waypoints, state.get.active)))(_ => ctx.reply(Done))
    }.onCommand[ChangeTrackMaintainer, Done] {
      case (ChangeTrackMaintainer(track), ctx, state) =>
        //TODO check if the maintainer exists as biker
        ctx.thenPersist(TrackMaintainerChanged(TrackState(state.get.id, state.get.name, track.maintainer.get,  state.get.waypoints, state.get.active)))(_ => ctx.reply(Done))
    }.onCommand[ActivateTrack, Done] {
      case (ActivateTrack(track), ctx, state) =>
        ctx.thenPersist(TrackActivated(TrackState(state.get.id, state.get.name, state.get.maintainer, state.get.waypoints, true)))(_ => ctx.reply(Done))
    }.onCommand[DeactivateTrack, Done] {
      case (DeactivateTrack(track), ctx, state) =>
        ctx.thenPersist(TrackDeactivated(TrackState(state.get.id, state.get.name, state.get.maintainer, state.get.waypoints, false)))(_ => ctx.reply(Done))
    }.onCommand[AddTrackWayPoint, Done] {
      case (AddTrackWayPoint(trackID,waypoint), ctx, state) =>
        val waypoints = state.get.waypoints :+ waypoint
        ctx.thenPersist(TrackWayPointAdded(TrackState(state.get.id, state.get.name, state.get.maintainer, waypoints, state.get.active)))(_ => ctx.reply(Done))
    }.onCommand[RemoveTrackWayPoint, Done] {
      case (RemoveTrackWayPoint(trackID,waypointID), ctx, state) =>
        val waypoints = state.get.waypoints.filterNot(waypoint => (waypoint.id==waypointID))
        //println(s"waypoints after remove are ${waypoints}")
        ctx.thenPersist(TrackWayPointRemoved(TrackState(state.get.id, state.get.name, state.get.maintainer, waypoints, state.get.active)))(_ => ctx.reply(Done))
    }.onCommand[MarkWayPointInitial, Done] {
      case (MarkWayPointInitial(trackID,waypointID), ctx, state) =>

        val ways = state.get.waypoints
        //TODO check if .last is the best option while getting the only element of a sequence
        val initialWay = ways.filter(waypoint => (waypoint.id==waypointID)).last
        def rotateSequenceLeft[A](seq: Seq[A], i: Int): Seq[A] = {
          if (seq.isEmpty) seq
          else {
            val size = seq.size
            seq.drop(i % size) ++ seq.take(i % size)
          }
        }
        val rotatedWays = rotateSequenceLeft(ways, ways.indexOf(initialWay))
        ctx.thenPersist(TrackInitialWayPointMarked(TrackState(state.get.id, state.get.name, state.get.maintainer, rotatedWays, state.get.active)))(_ => ctx.reply(Done))
    }.onEvent {
      case (TrackNameChanged(track), state) =>
        Some(track)
      case (TrackMaintainerChanged(track), state) =>
        Some(track)
      case (TrackActivated(track), state) =>
        Some(track)
      case (TrackDeactivated(track), state) =>
        Some(track)
      case (TrackWayPointAdded(track), state) =>
        Some(track)
      case (TrackWayPointRemoved(track), state) =>
        Some(track)
      case (TrackInitialWayPointMarked(track), state) =>
        Some(track)
    }
  }

  private val initial: Actions = {
    Actions().onReadOnlyCommand[GetTrack.type, Option[TrackState]] {
      case (GetTrack, ctx, state) => ctx.reply(state)
    }.onCommand[CreateTrack, Done] {
      case (CreateTrack(track), ctx, state) =>
        //TODO check if the maintainer exists as biker
        ctx.thenPersist(TrackCreated(track))( _ => ctx.reply(Done))
    }.onReadOnlyCommand[ChangeTrackName, Done] {
      case (ChangeTrackName(track), ctx, state) =>
        ctx.invalidCommand(s"Track {$track.id} does not exist")
    }.onReadOnlyCommand[ChangeTrackMaintainer, Done] {
      case (ChangeTrackMaintainer(track), ctx, state) =>
        ctx.invalidCommand(s"Track {$track.id} does not exist")
    }.onReadOnlyCommand[ActivateTrack, Done] {
      case (ActivateTrack(track), ctx, state) =>
        ctx.invalidCommand(s"Track {$track.id} does not exist")
    }.onReadOnlyCommand[DeactivateTrack, Done] {
      case (DeactivateTrack(track), ctx, state) =>
        ctx.invalidCommand(s"Track {$track.id} does not exist")
    }.onReadOnlyCommand[AddTrackWayPoint, Done] {
      case (AddTrackWayPoint(trackID,waypoint), ctx, state) =>
        ctx.invalidCommand(s"Track {$trackID} does not exist")
    }.onReadOnlyCommand[RemoveTrackWayPoint, Done] {
      case (RemoveTrackWayPoint(trackID,waypointID), ctx, state) =>
        ctx.invalidCommand(s"Track {$trackID} does not exist")
    }.onReadOnlyCommand[MarkWayPointInitial, Done] {
      case (MarkWayPointInitial(trackID,waypointID), ctx, state) =>
        ctx.invalidCommand(s"Track {$trackID} does not exist")
    }.onEvent {
      case (TrackCreated(track), state) =>
        Some(TrackState(track.id,track.name,track.maintainer,track.waypoints,track.active))
    }
  }
}

