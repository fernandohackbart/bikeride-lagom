package com.bikeride.biker.impl

import java.util.UUID
import akka.Done
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import play.api.libs.json.{Format, Json}

sealed trait BikerCommand

case class CreateBiker(biker: BikerState) extends BikerCommand with ReplyType[Done]
object CreateBiker {
  implicit val format: Format[CreateBiker] = Json.format
}

case class ChangeBikerName(biker: BikerChange) extends BikerCommand with ReplyType[Done]
object ChangeBikerName {
  implicit val format: Format[ChangeBikerName] = Json.format
}

case class ChangeBikerAvatarB64(biker: BikerChange) extends BikerCommand with ReplyType[Done]
object ChangeBikerAvatarB64 {
  implicit val format: Format[ChangeBikerAvatarB64] = Json.format
}

case class ChangeBikerBloodType(biker: BikerChange) extends BikerCommand with ReplyType[Done]
object ChangeBikerBloodType {
  implicit val format: Format[ChangeBikerBloodType] = Json.format
}

case class ChangeBikerMobile(biker: BikerChange) extends BikerCommand with ReplyType[Done]
object ChangeBikerMobile {
  implicit val format: Format[ChangeBikerMobile] = Json.format
}

case class ChangeBikerEmail(biker: BikerChange) extends BikerCommand with ReplyType[Done]
object ChangeBikerEmail {
  implicit val format: Format[ChangeBikerEmail] = Json.format
}

case class ActivateBiker(bikerID: UUID) extends BikerCommand with ReplyType[Done]
object ActivateBiker {
  implicit val format: Format[ActivateBiker] = Json.format
}

case class DeactivateBiker(bikerID: UUID) extends BikerCommand with ReplyType[Done]
object DeactivateBiker {
  implicit val format: Format[DeactivateBiker] = Json.format
}

case object GetBiker extends BikerCommand with ReplyType[Option[BikerState]] {
  implicit val format: Format[GetBiker.type] = JsonFormats.singletonFormat(GetBiker)
}
