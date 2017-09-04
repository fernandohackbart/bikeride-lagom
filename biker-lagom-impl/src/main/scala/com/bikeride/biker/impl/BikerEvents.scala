package com.bikeride.biker.impl

import java.util.UUID
import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventShards, AggregateEventTag}
import play.api.libs.json.{Format, Json}

object BikerEvent {
  val NumShards = 3
  val Tags = AggregateEventTag.sharded[BikerEvent](NumShards)
}

sealed trait BikerEvent extends AggregateEvent[BikerEvent]
{
  override def aggregateTag: AggregateEventShards[BikerEvent] = BikerEvent.Tags
}

case class BikerCreated(biker: BikerState) extends BikerEvent
object BikerCreated {
  implicit val format: Format[BikerCreated] = Json.format
}

case class BikerNameChanged(biker: BikerState) extends BikerEvent
object BikerNameChanged {
  implicit val format: Format[BikerNameChanged] = Json.format
}

case class BikerAvatarB64Changed(biker: BikerState) extends BikerEvent
object BikerAvatarB64Changed {
  implicit val format: Format[BikerAvatarB64Changed] = Json.format
}

case class BikerBloodTypeChanged(biker: BikerState) extends BikerEvent
object BikerBloodTypeChanged {
  implicit val format: Format[BikerBloodTypeChanged] = Json.format
}

case class BikerMobileChanged(biker: BikerState) extends BikerEvent
object BikerMobileChanged {
  implicit val format: Format[BikerMobileChanged] = Json.format
}

case class BikerEmailChanged(biker: BikerState) extends BikerEvent
object BikerEmailChanged {
  implicit val format: Format[BikerEmailChanged] = Json.format
}

case class BikerActivated(biker: BikerState) extends BikerEvent
object BikerActivated {
  implicit val format: Format[BikerActivated] = Json.format
}

case class BikerDeactivated(biker: BikerState) extends BikerEvent
object BikerDeactivated {
  implicit val format: Format[BikerDeactivated] = Json.format
}
