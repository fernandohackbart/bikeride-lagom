package com.bikeride.authentication.api

import java.util.UUID
import play.api.libs.json.{Format, Json}

sealed trait AuthenticationEvent {
  val clientID: UUID
}

case class BikerLoggedIn(clientID: UUID,bikerID: UUID) extends AuthenticationEvent
object BikerLoggedIn {
  implicit val format: Format[BikerLoggedIn] = Json.format
}