package com.bikeride.biker.impl

import java.util.UUID
import play.api.libs.json._

case class BikerState (id: UUID,
                       name: String,
                       avatarb64: Option[String] = None,
                       bloodType: Option[String] = None,
                       mobile: Option[String] = None,
                       email: Option[String] = None,
                       active: Boolean)
object BikerState {
  implicit val format: Format[BikerState] = Json.format
}