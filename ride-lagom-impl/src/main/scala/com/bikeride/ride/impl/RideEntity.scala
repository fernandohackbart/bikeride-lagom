package com.bikeride.ride.impl


import akka.Done
import java.time.Instant._
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity

class RideEntity extends PersistentEntity {
  override type Command = RideCommand
  override type Event = RideEvent
  override type State = Option[RideState]

  override def initialState: Option[RideState] = None

  override def behavior: Behavior = {
    case Some(ride)  => postAdded
    case None => initial
  }

  private val postAdded: Actions = {
    Actions().onReadOnlyCommand[GetRide.type, Option[RideState]] {
      case (GetRide, ctx, state) => ctx.reply(state)
    }.onReadOnlyCommand[CreateRide, Done] {
      case (CreateRide(ride), ctx, state) =>
        ctx.invalidCommand(s"Ride with id $ride.id already exists")
    }.onCommand[ChangeRideName, Done] {
      case (ChangeRideName(ride), ctx, state) =>
        ctx.thenPersist(RideNameChanged(RideState(
          state.get.id,
          ride.name.get,
          state.get.organizer,
          state.get.limit,
          state.get.track,
          state.get.open,
          state.get.started,
          state.get.suspended,
          state.get.finished,
          state.get.leaders,
          state.get.riders,
          state.get.opentime,
          state.get.closetime,
          state.get.starttime,
          state.get.finishtime)))(_ => ctx.reply(Done))
    }.onCommand[ChangeRideOrganizer, Done] {
      case (ChangeRideOrganizer(ride), ctx, state) =>
        ctx.thenPersist(RideOrganizerChanged(RideState(
          state.get.id,
          state.get.name,
          //TODO check if the organizer exists as biker
          ride.organizer.get,
          state.get.limit,
          state.get.track,
          state.get.open,
          state.get.started,
          state.get.suspended,
          state.get.finished,
          state.get.leaders,
          state.get.riders,
          state.get.opentime,
          state.get.closetime,
          state.get.starttime,
          state.get.finishtime)))(_ => ctx.reply(Done))
    }.onCommand[ChangeRideTrack, Done] {
      case (ChangeRideTrack(ride), ctx, state) =>
        ctx.thenPersist(RideTrackChanged(RideState(
          state.get.id,
          state.get.name,
          state.get.organizer,
          state.get.limit,
          //TODO check if the track exists as track
          ride.track.get,
          state.get.open,
          state.get.started,
          state.get.suspended,
          state.get.finished,
          state.get.leaders,
          state.get.riders,
          state.get.opentime,
          state.get.closetime,
          state.get.starttime,
          state.get.finishtime)))(_ => ctx.reply(Done))
    }.onCommand[ChangeRideLimit, Done] {
      case (ChangeRideLimit(ride), ctx, state) =>
        ctx.thenPersist(RideLimitChanged(RideState(
          state.get.id,
          state.get.name,
          state.get.organizer,
          ride.limit.get,
          state.get.track,
          state.get.open,
          state.get.started,
          state.get.suspended,
          state.get.finished,
          state.get.leaders,
          state.get.riders,
          state.get.opentime,
          state.get.closetime,
          state.get.starttime,
          state.get.finishtime)))(_ => ctx.reply(Done))
    }.onCommand[AddRideLeader, Done] {
      case (AddRideLeader(rideID,leaderID), ctx, state) =>
        val leaders = state.get.leaders :+ leaderID
        ctx.thenPersist(RideLeadersChanged(RideState(
          state.get.id,
          state.get.name,
          state.get.organizer,
          state.get.limit,
          state.get.track,
          state.get.open,
          state.get.started,
          state.get.suspended,
          state.get.finished,
          leaders,
          state.get.riders,
          state.get.opentime,
          state.get.closetime,
          state.get.starttime,
          state.get.finishtime)))(_ => ctx.reply(Done))
    }.onCommand[RemoveRideLeader, Done] {
      case (RemoveRideLeader(rideID,leaderID), ctx, state) =>
        val leaders = state.get.leaders.filterNot(leader => leader==leaderID)
        ctx.thenPersist(RideLeadersChanged(RideState(
          state.get.id,
          state.get.name,
          state.get.organizer,
          state.get.limit,
          state.get.track,
          state.get.open,
          state.get.started,
          state.get.suspended,
          state.get.finished,
          leaders,
          state.get.riders,
          state.get.opentime,
          state.get.closetime,
          state.get.starttime,
          state.get.finishtime)))(_ => ctx.reply(Done))
    }.onCommand[OpenSubscriptionsToRide, Done] {
      case (OpenSubscriptionsToRide(rideID), ctx, state) =>
        ctx.thenPersist(RideSubscriptionsOpened(RideState(
          state.get.id,
          state.get.name,
          state.get.organizer,
          state.get.limit,
          state.get.track,
          true,
          state.get.started,
          state.get.suspended,
          state.get.finished,
          state.get.leaders,
          state.get.riders,
          Some(now),
          state.get.closetime,
          state.get.starttime,
          state.get.finishtime)))(_ => ctx.reply(Done))
    }.onCommand[CloseSubscriptionsToRide, Done] {
      case (CloseSubscriptionsToRide(rideID), ctx, state) =>
        ctx.thenPersist(RideSubscriptionsClosed(RideState(
          state.get.id,
          state.get.name,
          state.get.organizer,
          state.get.limit,
          state.get.track,
          false,
          state.get.started,
          state.get.suspended,
          state.get.finished,
          state.get.leaders,
          state.get.riders,
          state.get.opentime,
          Some(now),
          state.get.starttime,
          state.get.finishtime)))(_ => ctx.reply(Done))
    }.onCommand[SubscribeRideRider, Done] {
      case (SubscribeRideRider(rideID,bikerID), ctx, state) =>
        val riders = state.get.riders :+ bikerID
        ctx.thenPersist(RideRiderSubscribed(RideState(
          state.get.id,
          state.get.name,
          state.get.organizer,
          state.get.limit,
          state.get.track,
          state.get.open,
          state.get.started,
          state.get.suspended,
          state.get.finished,
          state.get.leaders,
          riders,
          state.get.opentime,
          state.get.closetime,
          state.get.starttime,
          state.get.finishtime)))(_ => ctx.reply(Done))
    }.onCommand[UnsubscribeRideRider, Done] {
      case (UnsubscribeRideRider(rideID,bikerID), ctx, state) =>
        //TODO: is required to know which biker unsubscribed?
        val riders = state.get.riders.filterNot(rider => rider==bikerID)
        ctx.thenPersist(RideRiderUnsubscribed(RideState(
          state.get.id,
          state.get.name,
          state.get.organizer,
          state.get.limit,
          state.get.track,
          state.get.open,
          state.get.started,
          state.get.suspended,
          state.get.finished,
          state.get.leaders,
          riders,
          state.get.opentime,
          state.get.closetime,
          state.get.starttime,
          state.get.finishtime)))(_ => ctx.reply(Done))
    }.onCommand[StartRide, Done] {
      case (StartRide(rideID), ctx, state) =>
        ctx.thenPersist(RideStarted(RideState(
          state.get.id,
          state.get.name,
          state.get.organizer,
          state.get.limit,
          state.get.track,
          state.get.open,
          true,
          state.get.suspended,
          state.get.finished,
          state.get.leaders,
          state.get.riders,
          state.get.opentime,
          state.get.closetime,
          Some(now),
          state.get.finishtime)))(_ => ctx.reply(Done))
    }.onCommand[SuspendRide, Done] {
      case (SuspendRide(rideID), ctx, state) =>
        ctx.thenPersist(RideSuspended(RideState(
          state.get.id,
          state.get.name,
          state.get.organizer,
          state.get.limit,
          state.get.track,
          state.get.open,
          state.get.started,
          true,
          state.get.finished,
          state.get.leaders,
          state.get.riders,
          state.get.opentime,
          state.get.closetime,
          state.get.starttime,
          state.get.finishtime)))(_ => ctx.reply(Done))
    }.onCommand[ResumeRide, Done] {
      case (ResumeRide(rideID), ctx, state) =>
        ctx.thenPersist(RideResumed(RideState(
          state.get.id,
          state.get.name,
          state.get.organizer,
          state.get.limit,
          state.get.track,
          state.get.open,
          state.get.started,
          false,
          state.get.finished,
          state.get.leaders,
          state.get.riders,
          state.get.opentime,
          state.get.closetime,
          state.get.starttime,
          state.get.finishtime)))(_ => ctx.reply(Done))
    }.onCommand[FinishRide, Done] {
      case (FinishRide(rideID), ctx, state) =>
        ctx.thenPersist(RideFinished(RideState(
          state.get.id,
          state.get.name,
          state.get.organizer,
          state.get.limit,
          state.get.track,
          state.get.open,
          state.get.started,
          state.get.suspended,
          true,
          state.get.leaders,
          state.get.riders,
          state.get.opentime,
          state.get.closetime,
          state.get.starttime,
          Some(now))))(_ => ctx.reply(Done))
    }.onEvent {
      case (RideNameChanged(ride), state) =>
        Some(ride)
      case (RideOrganizerChanged(ride), state) =>
        Some(ride)
      case (RideTrackChanged(ride), state) =>
        Some(ride)
      case (RideLimitChanged(ride), state) =>
        Some(ride)
      case (RideLeadersChanged(ride), state) =>
        Some(ride)
      case (RideSubscriptionsOpened(ride), state) =>
        Some(ride)
      case (RideSubscriptionsClosed(ride), state) =>
        Some(ride)
      case (RideRiderSubscribed(ride), state) =>
        Some(ride)
      case (RideRiderUnsubscribed(ride), state) =>
        Some(ride)
      case (RideStarted(ride), state) =>
        Some(ride)
      case (RideSuspended(ride), state) =>
        Some(ride)
      case (RideResumed(ride), state) =>
        Some(ride)
      case (RideFinished(ride), state) =>
        Some(ride)
    }
  }

  private val initial: Actions = {
    Actions().onReadOnlyCommand[GetRide.type, Option[RideState]] {
      case (GetRide, ctx, state) => ctx.reply(state)
    }.onCommand[CreateRide, Done] {
      case (CreateRide(ride), ctx, state) =>
        //TODO check if the maintainer exists as biker
        ctx.thenPersist(RideCreated(ride))( _ => ctx.reply(Done))
    }.onReadOnlyCommand[ChangeRideName, Done] {
      case (ChangeRideName(ride), ctx, state) =>
        ctx.invalidCommand(s"Ride {$ride.id} does not exist")
    }.onReadOnlyCommand[ChangeRideOrganizer, Done] {
      case (ChangeRideOrganizer(ride), ctx, state) =>
        ctx.invalidCommand(s"Ride {$ride.id} does not exist")
    }.onReadOnlyCommand[ChangeRideTrack, Done] {
      case (ChangeRideTrack(ride), ctx, state) =>
        ctx.invalidCommand(s"Ride {$ride.id} does not exist")
    }.onReadOnlyCommand[ChangeRideLimit, Done] {
      case (ChangeRideLimit(ride), ctx, state) =>
        ctx.invalidCommand(s"Ride {$ride.id} does not exist")
    }.onReadOnlyCommand[AddRideLeader, Done] {
      case (AddRideLeader(rideID,leaderID), ctx, state) =>
        ctx.invalidCommand(s"Ride {$rideID} does not exist")
    }.onReadOnlyCommand[RemoveRideLeader, Done] {
      case (RemoveRideLeader(rideID,leaderID), ctx, state) =>
        ctx.invalidCommand(s"Ride {$rideID} does not exist")
    }.onReadOnlyCommand[OpenSubscriptionsToRide, Done] {
      case (OpenSubscriptionsToRide(rideID), ctx, state) =>
        ctx.invalidCommand(s"Ride {$rideID} does not exist")
    }.onReadOnlyCommand[CloseSubscriptionsToRide, Done] {
      case (CloseSubscriptionsToRide(rideID), ctx, state) =>
        ctx.invalidCommand(s"Ride {$rideID} does not exist")
    }.onReadOnlyCommand[SubscribeRideRider, Done] {
      case (SubscribeRideRider(rideID,riderID), ctx, state) =>
        ctx.invalidCommand(s"Ride {$rideID} does not exist")
    }.onReadOnlyCommand[UnsubscribeRideRider, Done] {
      case (UnsubscribeRideRider(rideID,riderID), ctx, state) =>
        ctx.invalidCommand(s"Ride {$rideID} does not exist")
    }.onReadOnlyCommand[StartRide, Done] {
      case (StartRide(rideID), ctx, state) =>
        ctx.invalidCommand(s"Ride {$rideID} does not exist")
    }.onReadOnlyCommand[SuspendRide, Done] {
      case (SuspendRide(rideID), ctx, state) =>
        ctx.invalidCommand(s"Ride {$rideID} does not exist")
    }.onReadOnlyCommand[ResumeRide, Done] {
      case (ResumeRide(rideID), ctx, state) =>
        ctx.invalidCommand(s"Ride {$rideID} does not exist")
    }.onReadOnlyCommand[FinishRide, Done] {
      case (FinishRide(rideID), ctx, state) =>
        ctx.invalidCommand(s"Ride {$rideID} does not exist")
    }.onEvent {
      case (RideCreated(ride), state) =>
        Some(RideState(
          ride.id,
          ride.name,
          ride.organizer,
          ride.limit,
          ride.track,
          ride.open,
          ride.started,
          ride.suspended,
          ride.finished,
          ride.leaders,
          ride.riders,
          ride.opentime,
          ride.closetime,
          ride.starttime,
          ride.finishtime))
    }
  }
}

