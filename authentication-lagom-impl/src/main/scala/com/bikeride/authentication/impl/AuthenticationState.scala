package com.bikeride.authentication.impl

import java.util.UUID
import java.time.Instant
import play.api.libs.json.{Format, Json}

case class AuthenticationPINState (pinID: String,
                                   clientID: UUID,
                                   bikerID: UUID,
                                   bikerName: String,
                                   expiration: Instant)
object AuthenticationPINState {
  implicit val format: Format[AuthenticationPINState] = Json.format
}