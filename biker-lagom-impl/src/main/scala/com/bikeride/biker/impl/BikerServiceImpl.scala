package com.bikeride.biker.impl

import java.util.UUID

import akka.{Done, NotUsed}
import akka.stream.Materializer
import akka.stream.scaladsl.{Flow, Source}
import com.bikeride.biker.api
import com.bikeride.biker.api.BikerService
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.transport.NotFound
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.{EventStreamElement, PersistentEntityRegistry, ReadSide}
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraSession
import scala.collection.immutable
import scala.concurrent.{ExecutionContext, Future}

class BikerServiceImpl (bikerService: BikerService,
                        persistentEntityRegistry: PersistentEntityRegistry,
                        session: CassandraSession,
                        readside: ReadSide)
                        (implicit ec: ExecutionContext, mat: Materializer) extends BikerService{

  private def refFor(id: UUID) = persistentEntityRegistry.refFor[BikerEntity](id.toString)

  override def createBiker() = ServiceCall { req =>
    val bikerId = UUID.randomUUID()
    refFor(bikerId).ask(CreateBiker(BikerState(bikerId,req.name,req.avatarb64,req.bloodType,req.mobile,req.email,req.active))).map { _ =>
      api.BikerID(bikerId)
    }
  }

  override def changeBikerName(bikerId: UUID) = ServiceCall { req =>
    refFor(bikerId).ask(ChangeBikerName(BikerState(bikerId,req.name,req.avatarb64,req.bloodType,req.mobile,req.email,req.active))).map { _ =>
      api.BikerID(bikerId)
    }
  }

  override def changeBikerAvatar64(bikerId: UUID) = ServiceCall { req =>
    refFor(bikerId).ask(ChangeBikerAvatarB64(BikerState(bikerId,req.name,req.avatarb64,req.bloodType,req.mobile,req.email,req.active))).map { _ =>
      api.BikerID(bikerId)
    }
  }

  override def changeBikerBloodType(bikerId: UUID) = ServiceCall { req =>
    refFor(bikerId).ask(ChangeBikerBloodType(BikerState(bikerId,req.name,req.avatarb64,req.bloodType,req.mobile,req.email,req.active))).map { _ =>
      api.BikerID(bikerId)
    }
  }

  override def changeBikerMobile(bikerId: UUID) = ServiceCall { req =>
    refFor(bikerId).ask(ChangeBikerEmail(BikerState(bikerId,req.name,req.avatarb64,req.bloodType,req.mobile,req.email,req.active))).map { _ =>
      api.BikerID(bikerId)
    }
  }

  override def changeBikerEmail(bikerId: UUID) = ServiceCall { req =>
    refFor(bikerId).ask(ChangeBikerEmail(BikerState(bikerId,req.name,req.avatarb64,req.bloodType,req.mobile,req.email,req.active))).map { _ =>
      api.BikerID(bikerId)
    }
  }

  override def activateBiker(bikerId: UUID) = ServiceCall { req =>
    refFor(bikerId).ask(ActivateBiker(bikerId)).map { _ =>
      api.BikerID(bikerId)
    }
  }

  override def deactivateBiker(bikerId: UUID) = ServiceCall { req =>
    refFor(bikerId).ask(DeactivateBiker(bikerId)).map { _ =>
      api.BikerID(bikerId)
    }
  }

  override def getBikerIsActive(bikerId: UUID) = ServiceCall { _ =>
    refFor(bikerId).ask(GetBiker).map {
      case Some(biker) =>
        api.BikerIsActive(bikerId,biker.active)
      case None =>
        throw NotFound(s"Biker with id $bikerId")
    }
  }

  override def getBikerAvatarB64(bikerId: UUID) = ServiceCall { _ =>
    refFor(bikerId).ask(GetBiker).map {
      case Some(biker) =>
        api.BikerAvatarB64(bikerId,biker.avatarb64)
      case None =>
        throw NotFound(s"Biker with id $bikerId")
    }
  }

  override def getBiker(bikerId: UUID) = ServiceCall { _ =>
    refFor(bikerId).ask(GetBiker).map {
      case Some(biker) =>
        api.Biker(api.BikerID(bikerId),api.BikerFields(biker.name,biker.avatarb64,biker.bloodType,biker.mobile,biker.email,biker.active))
      case None =>
        throw NotFound(s"Biker with id $bikerId")
    }
  }

  private val DefaultPageSize = 10

  //TODO maps the Option fields
  override def getBikers( pageNo: Option[Int], pageSize: Option[Int]) = ServiceCall[NotUsed, Seq[api.Biker]] { req =>

    println(s"getBikers(${pageNo.getOrElse(0)}, ${pageSize.getOrElse(DefaultPageSize)})   ##############")
    //val offset = pageNo * pageSize
    //.drop(offset)
    session.select("SELECT name,avatarb64,bloodType,mobile,email,active FROM bikers LIMIT ?",Integer.valueOf(pageSize.getOrElse(DefaultPageSize))).map { row =>
      api.Biker(
        api.BikerID(row.getUUID("id")),
        api.BikerFields(
          row.getString("name"),
          Some(row.getString("avatarb64")),
          Some(row.getString("bloodtype")),
          Some(row.getString("mobile")),
          Some(row.getString("email")),
          row.getBool("active")
        )
      )
    }.runFold(Seq.empty[api.Biker])((acc, e) => acc :+ e)
  }

  //TODO maps the Option fields
  //TODO cut in pages and jump to the right one
  override def searchBikers = ServiceCall[api.BikerQueryFields, Seq[api.Biker]] { req =>
    session.select("SELECT name,avatarb64,bloodType,mobile,email,active FROM bikers").map { row =>
      api.Biker(
        api.BikerID(row.getUUID("id")),
        api.BikerFields(
          row.getString("name"),
          Some(row.getString("avatarb64")),
          Some(row.getString("bloodtype")),
          Some(row.getString("mobile")),
          Some(row.getString("email")),
          row.getBool("active")
        )
      )
    }.runFold(Seq.empty[api.Biker])((acc, e) => acc :+ e)
  }
}