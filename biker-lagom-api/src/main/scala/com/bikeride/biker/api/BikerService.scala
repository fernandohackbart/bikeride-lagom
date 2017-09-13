package com.bikeride.biker.api

import java.util.UUID
import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import play.api.libs.json.{Format, Json}
import scala.collection.Seq

trait BikerService extends Service {

  def createBiker: ServiceCall[BikerFields, BikerID]
  def changeBikerName(id: UUID): ServiceCall[BikerChangeFields, BikerID]
  def changeBikerAvatarB64(id: UUID): ServiceCall[BikerChangeFields, BikerID]
  def changeBikerBloodType(id: UUID): ServiceCall[BikerChangeFields, BikerID]
  def changeBikerMobile(id: UUID): ServiceCall[BikerChangeFields, BikerID]
  def changeBikerEmail(id: UUID): ServiceCall[BikerChangeFields, BikerID]
  def activateBiker(id: UUID): ServiceCall[NotUsed, BikerID]
  def deactivateBiker(id: UUID): ServiceCall[NotUsed, BikerID]
  def getBikerIsActive(id: UUID): ServiceCall[NotUsed, BikerIsActive]
  def getBikerAvatarB64(id: UUID): ServiceCall[NotUsed, BikerAvatarB64]
  def getBiker(id: UUID): ServiceCall[NotUsed, Biker]
  def getBikers(pageNo: Option[Int], pageSize: Option[Int]): ServiceCall[NotUsed,Seq[Biker]]

  override final def descriptor = {
    import Service._
    named("biker")
      .withCalls(
        restCall(Method.POST,"/api/biker", createBiker),
        restCall(Method.PUT,"/api/biker/:id/name", changeBikerName _),
        restCall(Method.PUT,"/api/biker/:id/avatar64", changeBikerAvatarB64 _),
        restCall(Method.PUT,"/api/biker/:id/bloodtype", changeBikerBloodType _),
        restCall(Method.PUT,"/api/biker/:id/mobile", changeBikerMobile _),
        restCall(Method.PUT,"/api/biker/:id/email", changeBikerEmail _),
        restCall(Method.POST,"/api/biker/:id/activate", activateBiker _),
        restCall(Method.POST,"/api/biker/:id/deactivate", deactivateBiker _),
        restCall(Method.GET,"/api/biker/:id/isactive", getBikerIsActive _),
        restCall(Method.GET,"/api/biker/:id/avatarb64", getBikerAvatarB64 _),
        restCall(Method.GET,"/api/biker/:id", getBiker _),
        restCall(Method.GET,"/api/bikers?pageNo&pageSize", getBikers _)
      ).withAutoAcl(true)
  }
}

case class BikerID(id: UUID)
object  BikerID {
  implicit val format: Format[BikerID] = Json.format
}

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

case class BikerAvatarB64(id: UUID, avatarb64: Option[String] = None)
object  BikerAvatarB64 {
  implicit val format: Format[BikerAvatarB64] = Json.format
}

case class BikerIsActive(id: UUID, active: Boolean)
object  BikerIsActive {
  implicit val format: Format[BikerIsActive] = Json.format
}
