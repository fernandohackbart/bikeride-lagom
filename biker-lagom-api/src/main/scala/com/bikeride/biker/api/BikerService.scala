package com.bikeride.biker.api

import java.util.UUID

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import play.api.libs.json.{Format, Json}

import scala.collection.Seq
import com.bikeride.utils.security.{SecurityHeaderFilter, Token}

trait BikerService extends Service {

  def createBiker: ServiceCall[BikerCreateRequest, BikerToken]
  //TODO create endpoint for token refresh
  //TODO create endpoint for token reset
  def changeBikerName(bikerID: UUID): ServiceCall[BikerChangeFields, BikerID]
  def changeBikerAvatarB64(bikerID: UUID): ServiceCall[BikerChangeFields, BikerID]
  def changeBikerBloodType(bikerID: UUID): ServiceCall[BikerChangeFields, BikerID]
  def changeBikerMobile(bikerID: UUID): ServiceCall[BikerChangeFields, BikerID]
  def changeBikerEmail(bikerID: UUID): ServiceCall[BikerChangeFields, BikerID]
  def activateBiker(bikerID: UUID): ServiceCall[NotUsed, BikerID]
  def deactivateBiker(bikerID: UUID): ServiceCall[NotUsed, BikerID]
  def getBikerIsActive(bikerID: UUID): ServiceCall[NotUsed, BikerIsActive]
  def getBikerAvatarB64(bikerID: UUID): ServiceCall[NotUsed, BikerAvatarB64]
  def getBiker(bikerID: UUID): ServiceCall[NotUsed, Biker]
  def getBikers(pageNo: Option[Int], pageSize: Option[Int]): ServiceCall[NotUsed,Seq[Biker]]

  override final def descriptor = {
    import Service._
    named("biker")
      .withCalls(
        restCall(Method.POST,"/api/biker", createBiker),
        //TODO create endpoint for token refresh
        //TODO create endpoint for token reset
        restCall(Method.PUT,"/api/biker/:bikerID/name", changeBikerName _),
        restCall(Method.PUT,"/api/biker/:bikerID/avatar64", changeBikerAvatarB64 _),
        restCall(Method.PUT,"/api/biker/:bikerID/bloodtype", changeBikerBloodType _),
        restCall(Method.PUT,"/api/biker/:bikerID/mobile", changeBikerMobile _),
        restCall(Method.PUT,"/api/biker/:bikerID/email", changeBikerEmail _),
        restCall(Method.POST,"/api/biker/:bikerID/activate", activateBiker _),
        restCall(Method.POST,"/api/biker/:bikerID/deactivate", deactivateBiker _),
        restCall(Method.GET,"/api/biker/:bikerID/isactive", getBikerIsActive _),
        restCall(Method.GET,"/api/biker/:bikerID/avatarb64", getBikerAvatarB64 _),
        restCall(Method.GET,"/api/biker/:bikerID", getBiker _),
        restCall(Method.GET,"/api/bikers?pageNo&pageSize", getBikers _)
      ).withAutoAcl(true)
      //.withHeaderFilter(SecurityHeaderFilter.Composed)
  }
}

case class BikerToken(bikerID: BikerID,token: Token)
object  BikerToken {
  implicit val format: Format[BikerToken] = Json.format
}

case class BikerClient(clientId: UUID)
object  BikerClient {
  implicit val format: Format[BikerClient] = Json.format
}

case class BikerCreateRequest(clientId: BikerClient,bikerFields: BikerFields)
object  BikerCreateRequest {
  implicit val format: Format[BikerCreateRequest] = Json.format
}

case class BikerID(bikerID: UUID)
object  BikerID {
  implicit val format: Format[BikerID] = Json.format
}

//TODO email is not anymore optional
case class BikerFields(name: String,
                       avatarb64: Option[String] = None,
                       bloodType: Option[String] = None,
                       mobile: Option[String] = None,
                       email: Option[String] = None,
                       active: Boolean = true)
object  BikerFields {
  implicit val format: Format[BikerFields] = Json.format
}


case class BikerChangeFields(name: Option[String] = None,
                             avatarb64: Option[String] = None,
                             bloodType: Option[String] = None,
                             mobile: Option[String] = None,
                             email: Option[String] = None)
object  BikerChangeFields {
  implicit val format: Format[BikerChangeFields] = Json.format
}

case class Biker(bikerID: BikerID, bikerFields: BikerFields)
object  Biker {
  implicit val format: Format[Biker] = Json.format
}

case class BikerAvatarB64(bikerID: UUID, avatarb64: Option[String] = None)
object  BikerAvatarB64 {
  implicit val format: Format[BikerAvatarB64] = Json.format
}

case class BikerIsActive(bikerID: UUID, active: Boolean)
object  BikerIsActive {
  implicit val format: Format[BikerIsActive] = Json.format
}
