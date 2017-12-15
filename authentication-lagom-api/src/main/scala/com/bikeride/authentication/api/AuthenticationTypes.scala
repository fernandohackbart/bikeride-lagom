package com.bikeride.authentication.api

import com.bikeride.biker.api.{BikerClient, BikerToken}
import play.api.libs.json.{Format, Json}

case class RefreshTokenRequest(client: BikerClient,
                               bikerToken : BikerToken)
object  RefreshTokenRequest {
  implicit val format: Format[RefreshTokenRequest] = Json.format
}

case class ValidateTokenResponse(valid: Boolean = false)
object  ValidateTokenResponse {
  implicit val format: Format[ValidateTokenResponse] = Json.format
}

case class ValidatePINRequest(client: BikerClient,
                              pin: String)
object  ValidatePINRequest {
  implicit val format: Format[ValidatePINRequest] = Json.format
}

case class GeneratePINRequest(client: BikerClient,
                              email: Option[String] = None,
                              mobile: Option[String] = None)
object  GeneratePINRequest {
  implicit val format: Format[GeneratePINRequest] = Json.format
}

case class GeneratePINResponse(message: String)
object  GeneratePINResponse {
  implicit val format: Format[GeneratePINResponse] = Json.format
}