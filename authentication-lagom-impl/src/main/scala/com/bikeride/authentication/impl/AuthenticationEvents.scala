package com.bikeride.authentication.impl

import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventShards, AggregateEventTag}
import play.api.libs.json.{Format, Json}

object AuthenticationEvent {
  val NumShards = 3
  val Tags = AggregateEventTag.sharded[AuthenticationEvent](NumShards)
}

sealed trait AuthenticationEvent extends AggregateEvent[AuthenticationEvent]
{
  override def aggregateTag: AggregateEventShards[AuthenticationEvent] = AuthenticationEvent.Tags
}

case class AuthenticationPINGenerated(pin: AuthenticationPINState) extends AuthenticationEvent
object AuthenticationPINGenerated {
  implicit val format: Format[AuthenticationPINGenerated] = Json.format
}
