package com.bikeride.authentication.impl

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity

class AuthenticationEntity extends PersistentEntity {
  override type Command = AuthenticationCommand
  override type Event = AuthenticationEvent
  override type State = Option[AuthenticationPINState]

  override def initialState: Option[AuthenticationPINState] = None

  override def behavior: Behavior = {
    case Some(authnPIN) => defaultAction
    case None => defaultAction
  }

  private val defaultAction: Actions = {
    Actions().onCommand[GeneratePIN, Done] {
      case (GeneratePIN(authnPIN), ctx, state) =>
        ctx.thenPersist(AuthenticationPINGenerated(AuthenticationPINState(authnPIN.pinID,authnPIN.clientID,authnPIN.bikerID,authnPIN.bikerName,authnPIN.expiration)))(_ => ctx.reply(Done))
    }.onEvent {
      case (AuthenticationPINGenerated(authnPIN), state) =>
        Some(authnPIN)
    }
  }
}