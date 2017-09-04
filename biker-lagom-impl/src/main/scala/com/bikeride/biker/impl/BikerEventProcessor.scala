package com.bikeride.biker.impl

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import akka.Done
import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.PreparedStatement
import com.lightbend.lagom.scaladsl.persistence.AggregateEventTag
import com.lightbend.lagom.scaladsl.persistence.EventStreamElement
import com.lightbend.lagom.scaladsl.persistence.ReadSideProcessor
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraReadSide
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraSession
import scala.concurrent.Promise

class BikerEventProcessor(session: CassandraSession, readSide: CassandraReadSide)(implicit ec: ExecutionContext)
  extends ReadSideProcessor[BikerEvent] {

  override def aggregateTags: Set[AggregateEventTag[BikerEvent]] =  BikerEvent.Tags.allTags

  private def createTable(): Future[Done] =
    session.executeCreateTable("CREATE TABLE IF NOT EXISTS bikers ( " +
      "id UUID, " +
      "name TEXT, " +
      "avatarb64 TEXT, " +
      "bloodtype TEXT, " +
      "mobile TEXT, " +
      "email TEXT, " +
      "active BOOLEAN, " +
      "PRIMARY KEY (id))")

  private val insertBikerPromise = Promise[PreparedStatement]
  private def insertBiker: Future[PreparedStatement] = insertBikerPromise.future

  private val updateBikerNamePromise = Promise[PreparedStatement]
  private def updateBikerName: Future[PreparedStatement] = updateBikerNamePromise.future

  private val updateBikerAvatarB64Promise = Promise[PreparedStatement]
  private def updateBikerAvatarB64: Future[PreparedStatement] = updateBikerAvatarB64Promise.future

  private val updateBikerBloodTypePromise = Promise[PreparedStatement]
  private def updateBikerBloodType: Future[PreparedStatement] = updateBikerBloodTypePromise.future

  private val updateBikerMobilePromise = Promise[PreparedStatement]
  private def updateBikerMobile: Future[PreparedStatement] = updateBikerMobilePromise.future

  private val updateBikerEmailPromise = Promise[PreparedStatement]
  private def updateBikerEmail: Future[PreparedStatement] = updateBikerEmailPromise.future

  private val updateBikerActivePromise = Promise[PreparedStatement]
  private def updateBikerActive: Future[PreparedStatement] = updateBikerActivePromise.future

  private def prepareBikerStatements(): Future[Done] = {
    val insert = session.prepare("INSERT INTO bikers (id, name, avatarb64, bloodType, mobile, email, active) VALUES (?, ?, ?, ?, ?, ?, ?)")
    insertBikerPromise.completeWith(insert)
    insert.map(_ => Done)
    val updateName = session.prepare("UPDATE bikers SET name = ? where id = ?")
    updateBikerNamePromise.completeWith(updateName)
    updateName.map(_ => Done)

    val updateAvatarB64 = session.prepare("UPDATE bikers SET avatarb64 = ? where id = ?")
    updateBikerAvatarB64Promise.completeWith(updateAvatarB64)
    updateAvatarB64.map(_ => Done)

    val updateBloodType = session.prepare("UPDATE bikers SET bloodtype = ? where id = ?")
    updateBikerBloodTypePromise.completeWith(updateBloodType)
    updateBloodType.map(_ => Done)

    val updateMobile = session.prepare("UPDATE bikers SET mobile = ? where id = ?")
    updateBikerMobilePromise.completeWith(updateMobile)
    updateMobile.map(_ => Done)

    val updateEmail = session.prepare("UPDATE bikers SET email = ? where id = ?")
    updateBikerEmailPromise.completeWith(updateEmail)
    updateEmail.map(_ => Done)

    val updateActive = session.prepare("UPDATE bikers SET active = ? where id = ?")
    updateBikerActivePromise.completeWith(updateActive)
    updateActive.map(_ => Done)
  }

  private def processBikerCreated(eventElement: EventStreamElement[BikerCreated]): Future[List[BoundStatement]] = {
    insertBiker.map { ps =>
      val bindInsertBiker = ps.bind()
      bindInsertBiker.setUUID("id", eventElement.event.biker.id)
      bindInsertBiker.setString("name", eventElement.event.biker.name)
      bindInsertBiker.setString("avatarb64", eventElement.event.biker.avatarb64.get)
      bindInsertBiker.setString("bloodtype", eventElement.event.biker.bloodType.get)
      bindInsertBiker.setString("mobile", eventElement.event.biker.mobile.get)
      bindInsertBiker.setString("email", eventElement.event.biker.email.get)
      bindInsertBiker.setBool("active", eventElement.event.biker.active)
      List(bindInsertBiker)
    }
  }

  private def processBikerNameChanged(eventElement: EventStreamElement[BikerNameChanged]): Future[List[BoundStatement]] = {
    updateBikerName.map { ps =>
      val bindUpdateBikerName = ps.bind()
      bindUpdateBikerName.setUUID("id", eventElement.event.biker.id)
      bindUpdateBikerName.setString("name", eventElement.event.biker.name)
      List(bindUpdateBikerName)
    }
  }

  private def processBikerAvatarB64Changed(eventElement: EventStreamElement[BikerAvatarB64Changed]): Future[List[BoundStatement]] = {
    updateBikerAvatarB64.map { ps =>
      val bindUpdateBikerAvatarB64 = ps.bind()
      bindUpdateBikerAvatarB64.setUUID("id", eventElement.event.biker.id)
      bindUpdateBikerAvatarB64.setString("avatarb64", eventElement.event.biker.avatarb64.get)
      List(bindUpdateBikerAvatarB64)
    }
  }

  private def processBikerBloodTypeChanged(eventElement: EventStreamElement[BikerBloodTypeChanged]): Future[List[BoundStatement]] = {
    updateBikerBloodType.map { ps =>
      val bindUpdateBikerBloodType = ps.bind()
      bindUpdateBikerBloodType.setUUID("id", eventElement.event.biker.id)
      bindUpdateBikerBloodType.setString("bloodtype", eventElement.event.biker.bloodType.get)
      List(bindUpdateBikerBloodType)
    }
  }

  private def processBikerMobileChanged(eventElement: EventStreamElement[BikerMobileChanged]): Future[List[BoundStatement]] = {
    updateBikerMobile.map { ps =>
      val bindUpdateBikerMobile = ps.bind()
      bindUpdateBikerMobile.setUUID("id", eventElement.event.biker.id)
      bindUpdateBikerMobile.setString("mobile", eventElement.event.biker.mobile.get)
      List(bindUpdateBikerMobile)
    }
  }

  private def processBikerEmailChanged(eventElement: EventStreamElement[BikerEmailChanged]): Future[List[BoundStatement]] = {
    updateBikerEmail.map { ps =>
      val bindUpdateBikerEmail = ps.bind()
      bindUpdateBikerEmail.setUUID("id", eventElement.event.biker.id)
      bindUpdateBikerEmail.setString("email", eventElement.event.biker.email.get)
      List(bindUpdateBikerEmail)
    }
  }

  private def processBikerActivated(eventElement: EventStreamElement[BikerActivated]): Future[List[BoundStatement]] = {
    updateBikerActive.map { ps =>
      val bindUpdateBikerActive = ps.bind()
      bindUpdateBikerActive.setUUID("id", eventElement.event.biker.id)
      bindUpdateBikerActive.setBool("active", eventElement.event.biker.active)
      List(bindUpdateBikerActive)
    }
  }

  private def processBikerDeactivated(eventElement: EventStreamElement[BikerDeactivated]): Future[List[BoundStatement]] = {
    updateBikerActive.map { ps =>
      val bindUpdateBikerDeactivated = ps.bind()
      bindUpdateBikerDeactivated.setUUID("id", eventElement.event.biker.id)
      bindUpdateBikerDeactivated.setBool("active", eventElement.event.biker.active)
      List(bindUpdateBikerDeactivated)
    }
  }

  override def buildHandler(): ReadSideProcessor.ReadSideHandler[BikerEvent] = {
    val builder = readSide.builder[BikerEvent]("bikersoffset")
    builder.setGlobalPrepare(createTable)
    builder.setPrepare(tag => prepareBikerStatements())
    builder.setEventHandler[BikerCreated](processBikerCreated)
    builder.setEventHandler[BikerNameChanged](processBikerNameChanged)
    builder.setEventHandler[BikerAvatarB64Changed](processBikerAvatarB64Changed)
    builder.setEventHandler[BikerBloodTypeChanged](processBikerBloodTypeChanged)
    builder.setEventHandler[BikerMobileChanged](processBikerMobileChanged)
    builder.setEventHandler[BikerEmailChanged](processBikerEmailChanged)
    builder.setEventHandler[BikerActivated](processBikerActivated)
    builder.setEventHandler[BikerDeactivated](processBikerDeactivated)
    builder.build()
  }
}