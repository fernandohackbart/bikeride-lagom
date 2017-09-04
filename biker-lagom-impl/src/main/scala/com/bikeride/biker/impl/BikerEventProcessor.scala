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
  private def prepareInsertBiker(): Future[Done] = {
    val f = session.prepare("INSERT INTO bikers (id, name, avatarb64, bloodType, mobile, email, active) VALUES (?, ?, ?, ?, ?, ?, ?)")
    insertBikerPromise.completeWith(f)
    f.map(_ => Done)
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

  private val updateBikerNamePromise = Promise[PreparedStatement]
  private def updateBikerName: Future[PreparedStatement] = updateBikerNamePromise.future
  private def prepareUpdateBikerName(): Future[Done] = {
    val f = session.prepare("UPDATE bikers SET name = ? where id = ?")
    updateBikerNamePromise.completeWith(f)
    f.map(_ => Done)
  }
  private def processBikerNameChanged(eventElement: EventStreamElement[BikerNameChanged]): Future[List[BoundStatement]] = {
    updateBikerName.map { ps =>
      val bindUpdateBikerName = ps.bind()
      bindUpdateBikerName.setUUID("id", eventElement.event.biker.id)
      bindUpdateBikerName.setString("name", eventElement.event.biker.name)
      List(bindUpdateBikerName)
    }
  }

  override def buildHandler(): ReadSideProcessor.ReadSideHandler[BikerEvent] = {
    val builder = readSide.builder[BikerEvent]("bikersoffset")
    builder.setGlobalPrepare(createTable)
    builder.setPrepare(tag => prepareInsertBiker())
    builder.setPrepare(tag => prepareUpdateBikerName())
    builder.setEventHandler[BikerCreated](processBikerCreated)
    builder.setEventHandler[BikerNameChanged](processBikerNameChanged)
    builder.build()
  }
}