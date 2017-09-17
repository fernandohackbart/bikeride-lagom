package com.bikeride.biker.impl

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity

class BikerEntity extends PersistentEntity {
  override type Command = BikerCommand
  override type Event = BikerEvent
  override type State = Option[BikerState]

  override def initialState: Option[BikerState] = None

  override def behavior: Behavior = {
    case Some(biker)  => postAdded
    case None => initial
  }

  private val postAdded: Actions = {
    Actions().onReadOnlyCommand[GetBiker.type, Option[BikerState]] {
      case (GetBiker, ctx, state) => ctx.reply(state)
    }.onReadOnlyCommand[CreateBiker, Done] {
      case (CreateBiker(biker), ctx, state) =>
        ctx.invalidCommand(s"Biker with id $biker.id already exists")
    }.onCommand[ChangeBikerName, Done] {
      case (ChangeBikerName(biker), ctx, state) =>
        ctx.thenPersist(BikerNameChanged(BikerState(state.get.id, biker.name.get, state.get.avatarb64, state.get.bloodType, state.get.mobile, state.get.email, state.get.active)))(_ => ctx.reply(Done))
    }.onCommand[ChangeBikerAvatarB64, Done] {
      case (ChangeBikerAvatarB64(biker), ctx, state) =>
        ctx.thenPersist(BikerAvatarB64Changed(BikerState(state.get.id, state.get.name, biker.avatarb64, state.get.bloodType, state.get.mobile, state.get.email, state.get.active)))(_ => ctx.reply(Done))
    }.onCommand[ChangeBikerBloodType, Done] {
      case (ChangeBikerBloodType(biker), ctx, state) =>
        ctx.thenPersist(BikerBloodTypeChanged(BikerState(state.get.id, state.get.name, state.get.avatarb64, biker.bloodType, state.get.mobile, state.get.email, state.get.active)))(_ => ctx.reply(Done))
    }.onCommand[ChangeBikerMobile, Done] {
      case (ChangeBikerMobile(biker), ctx, state) =>
        ctx.thenPersist(BikerMobileChanged(BikerState(state.get.id, state.get.name, state.get.avatarb64, state.get.bloodType, biker.mobile, state.get.email, state.get.active)))(_ => ctx.reply(Done))
    }.onCommand[ChangeBikerEmail, Done] {
      case (ChangeBikerEmail(biker), ctx, state) =>
        ctx.thenPersist(BikerEmailChanged(BikerState(state.get.id, state.get.name, state.get.avatarb64, state.get.bloodType, state.get.mobile, biker.email, state.get.active)))(_ => ctx.reply(Done))
    }.onCommand[ActivateBiker, Done] {
      case (ActivateBiker(biker), ctx, state) =>
        ctx.thenPersist(BikerActivated(BikerState(state.get.id, state.get.name, state.get.avatarb64, state.get.bloodType, state.get.mobile, state.get.email, true)))(_ => ctx.reply(Done))
    }.onCommand[DeactivateBiker, Done] {
      case (DeactivateBiker(biker), ctx, state) =>
        ctx.thenPersist(BikerDeactivated(BikerState(state.get.id, state.get.name, state.get.avatarb64, state.get.bloodType, state.get.mobile, state.get.email, false)))(_ => ctx.reply(Done))
    }.onEvent {
      case (BikerNameChanged(biker), state) =>
        Some(biker)
      case (BikerAvatarB64Changed(biker), state) =>
        Some(biker)
      case (BikerBloodTypeChanged(biker), state) =>
        Some(biker)
      case (BikerMobileChanged(biker), state) =>
        Some(biker)
      case (BikerEmailChanged(biker), state) =>
        Some(biker)
      case (BikerActivated(biker), state) =>
        Some(biker)
      case (BikerDeactivated(biker), state) =>
        Some(biker)
    }
  }

  private val initial: Actions = {
    Actions().onReadOnlyCommand[GetBiker.type, Option[BikerState]] {
      case (GetBiker, ctx, state) => ctx.reply(state)
    }.onCommand[CreateBiker, Done] {
      case (CreateBiker(biker), ctx, state) =>
        ctx.thenPersist(BikerCreated(biker))( _ => ctx.reply(Done))
    }.onReadOnlyCommand[ChangeBikerName, Done] {
      case (ChangeBikerName(biker), ctx, state) =>
        ctx.invalidCommand(s"Biker {$biker.id} does not exist")
    }.onReadOnlyCommand[ChangeBikerAvatarB64, Done] {
      case (ChangeBikerAvatarB64(biker), ctx, state) =>
        ctx.invalidCommand(s"Biker {$biker.id} does not exist")
    }.onReadOnlyCommand[ChangeBikerBloodType, Done] {
      case (ChangeBikerBloodType(biker), ctx, state) =>
        ctx.invalidCommand(s"Biker {$biker.id} does not exist")
    }.onReadOnlyCommand[ChangeBikerMobile, Done] {
      case (ChangeBikerMobile(biker), ctx, state) =>
        ctx.invalidCommand(s"Biker {$biker.id} does not exist")
    }.onReadOnlyCommand[ChangeBikerEmail, Done] {
      case (ChangeBikerEmail(biker), ctx, state) =>
        ctx.invalidCommand(s"Biker {$biker.id} does not exist")
    }.onReadOnlyCommand[ActivateBiker, Done] {
      case (ActivateBiker(biker), ctx, state) =>
        ctx.invalidCommand(s"Biker {$biker.id} does not exist")
    }.onReadOnlyCommand[DeactivateBiker, Done] {
      case (DeactivateBiker(biker), ctx, state) =>
        ctx.invalidCommand(s"Biker {$biker.id} does not exist")
    }.onEvent {
      case (BikerCreated(biker), state) =>
        Some(BikerState(biker.id,biker.name,biker.avatarb64,biker.bloodType,biker.mobile,biker.email,biker.active))
    }
  }
}