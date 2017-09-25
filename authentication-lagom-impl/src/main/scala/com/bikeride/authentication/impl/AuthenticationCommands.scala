package com.bikeride.authentication.impl

import akka.Done
import com.bikeride.utils.json.JSONFormats
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import play.api.libs.json.{Format, Json}

sealed trait AuthenticationCommand

case class GeneratePIN(pin: AuthenticationPINState) extends AuthenticationCommand with ReplyType[Done]
object GeneratePIN {
  implicit val format: Format[GeneratePIN] = Json.format
}

case object GetPIN extends AuthenticationCommand with ReplyType[Option[AuthenticationPINState]] {
  implicit val format: Format[GetPIN.type] = JSONFormats.singletonFormat(GetPIN)
}