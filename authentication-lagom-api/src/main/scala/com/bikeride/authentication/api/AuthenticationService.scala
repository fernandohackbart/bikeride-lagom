package com.bikeride.authentication.api

import com.bikeride.biker.api.{BikerCreateRequest, BikerToken}
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.broker.Topic

trait AuthenticationService extends Service {

  def createBiker: ServiceCall[BikerCreateRequest, BikerToken]
  def validatePIN: ServiceCall[ValidatePINRequest, BikerToken]
  def generatePIN: ServiceCall[GeneratePINRequest, GeneratePINResponse]
  def refreshClientToken: ServiceCall[RefreshTokenRequest, BikerToken]
  def validateClientToken: ServiceCall[BikerToken, ValidateTokenResponse]
  def authenticationTopic() : Topic [BikerLoggedIn]

  object AuthenticationService  {
    val TOPIC_NAME = "authentication"
  }

  override final def descriptor = {
    import Service._
    named("authn")
      .withCalls(
        restCall(Method.POST,"/api/authn/biker", createBiker),
        restCall(Method.POST,"/api/authn/validatepin", validatePIN _),
        restCall(Method.POST,"/api/authn/generatepin", generatePIN _),
        restCall(Method.POST,"/api/authn/refreshtoken", refreshClientToken),
        restCall(Method.POST,"/api/authn/validatetoken", validateClientToken)
      ).withTopics(
        topic(AuthenticationService.TOPIC_NAME, authenticationTopic)
      ).withAutoAcl(true)
  }
}
