package com.bikeride.authentication.api

import play.api.libs.json.{Format, Json}
import com.bikeride.biker.api.{BikerCreateRequest, BikerToken, BikerClient}
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import com.lightbend.lagom.scaladsl.api.transport.Method

trait AuthenticationService extends Service {

  def createBiker: ServiceCall[BikerCreateRequest, BikerToken]
  def validatePIN: ServiceCall[ValidatePINRequest, BikerToken]
  def generatePIN: ServiceCall[GeneratePINRequest, GeneratePINResponse]

  override final def descriptor = {
    import Service._
    named("authn")
      .withCalls(
        restCall(Method.POST,"/api/authn/biker", createBiker),
        restCall(Method.POST,"/api/authn/validatepin", validatePIN _),
        restCall(Method.POST,"/api/authn/generatepin", generatePIN _)
      ).withAutoAcl(true)
  }

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