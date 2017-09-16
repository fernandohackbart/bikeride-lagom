package com.bikeride.track.impl

import scala.concurrent.{Await, ExecutionContext, Future, Promise}
import akka.Done
import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.PreparedStatement
import com.lightbend.lagom.scaladsl.persistence.AggregateEventTag
import com.lightbend.lagom.scaladsl.persistence.EventStreamElement
import com.lightbend.lagom.scaladsl.persistence.ReadSideProcessor
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraReadSide
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraSession
import scala.concurrent.duration._

class TrackEventProcessor(session: CassandraSession, readSide: CassandraReadSide)(implicit ec: ExecutionContext)
  extends ReadSideProcessor[TrackEvent] {

  override def aggregateTags: Set[AggregateEventTag[TrackEvent]] =  TrackEvent.Tags.allTags

  private def createTable(): Future[Done] = {
    session.executeCreateTable("CREATE TABLE IF NOT EXISTS tracks ( " +
      "id UUID, " +
      "name TEXT, " +
      "maintainer UUID, " +
      "active BOOLEAN, " +
      "PRIMARY KEY (id))")

    session.executeCreateTable("CREATE TABLE IF NOT EXISTS trackwaypoints ( " +
      "id UUID, " +
      "trackid UUID, " +
      "position INT," +
      "name TEXT, " +
      "coordinates TEXT, " +
      "PRIMARY KEY (id,position)) WITH CLUSTERING ORDER BY (position ASC)")
  }

  private val insertTrackPromise = Promise[PreparedStatement]
  private def insertTrack: Future[PreparedStatement] = insertTrackPromise.future

  private val updateTrackNamePromise = Promise[PreparedStatement]
  private def updateTrackName: Future[PreparedStatement] = updateTrackNamePromise.future

  private val updateTrackMaintainerPromise = Promise[PreparedStatement]
  private def updateTrackMaintainer: Future[PreparedStatement] = updateTrackMaintainerPromise.future

  private val updateTrackActivePromise = Promise[PreparedStatement]
  private def updateTrackActive: Future[PreparedStatement] = updateTrackActivePromise.future

  private val deleteTrackWaypointPromise = Promise[PreparedStatement]
  private def deleteTrackWaypoint: Future[PreparedStatement] = deleteTrackWaypointPromise.future

  private val insertTrackWaypointPromise = Promise[PreparedStatement]
  private def insertTrackWaypoint: Future[PreparedStatement] = insertTrackWaypointPromise.future


  private def prepareTrackStatements(): Future[Done] = {
    val insert = session.prepare("INSERT INTO tracks (id, name, maintainer, active) VALUES (?, ?, ?, ?)")
    insertTrackPromise.completeWith(insert)
    insert.map(_ => Done)

    val updateName = session.prepare("UPDATE tracks SET name = ? where id = ?")
    updateTrackNamePromise.completeWith(updateName)
    updateName.map(_ => Done)

    val updateMaintainer = session.prepare("UPDATE tracks SET maintainer = ? where id = ?")
    updateTrackMaintainerPromise.completeWith(updateMaintainer)
    updateMaintainer.map(_ => Done)

    val updateActive = session.prepare("UPDATE tracks SET active = ? where id = ?")
    updateTrackActivePromise.completeWith(updateActive)
    updateActive.map(_ => Done)

    val deleteWaypoint = session.prepare("DELETE FROM trackwaypoints where id = ? and position = ?")
    deleteTrackWaypointPromise.completeWith(deleteWaypoint)
    deleteWaypoint.map(_ => Done)

    val insertWaypoint = session.prepare("INSERT INTO trackwaypoints (id, trackid, position, name, coordinates) VALUES (?, ?, ?, ?, ?)")
    insertTrackWaypointPromise.completeWith(insertWaypoint)
    insertTrackWaypoint.map(_ => Done)
  }

  private def processTrackCreated(eventElement: EventStreamElement[TrackCreated]): Future[List[BoundStatement]] = {
    insertTrack.map { ps =>
      val bindInsertTrack = ps.bind()
      bindInsertTrack.setUUID("id", eventElement.event.track.id)
      bindInsertTrack.setString("name", eventElement.event.track.name)
      bindInsertTrack.setUUID("maintainer", eventElement.event.track.maintainer)
      bindInsertTrack.setBool("active", eventElement.event.track.active)
      List(bindInsertTrack)
    }
  }

  private def processTrackNameChanged(eventElement: EventStreamElement[TrackNameChanged]): Future[List[BoundStatement]] = {
    updateTrackName.map { ps =>
      val bindUpdateTrackName = ps.bind()
      bindUpdateTrackName.setUUID("id", eventElement.event.track.id)
      bindUpdateTrackName.setString("name", eventElement.event.track.name)
      List(bindUpdateTrackName)
    }
  }

  private def processTrackMaintainerChanged(eventElement: EventStreamElement[TrackMaintainerChanged]): Future[List[BoundStatement]] = {
    updateTrackMaintainer.map { ps =>
      val bindUpdateTrackMaintainer = ps.bind()
      bindUpdateTrackMaintainer.setUUID("id", eventElement.event.track.id)
      bindUpdateTrackMaintainer.setUUID("maintainer", eventElement.event.track.maintainer)
      List(bindUpdateTrackMaintainer)
    }
  }

  private def processTrackActivated(eventElement: EventStreamElement[TrackActivated]): Future[List[BoundStatement]] = {
    updateTrackActive.map { ps =>
      val bindUpdateTrackActived = ps.bind()
      bindUpdateTrackActived.setUUID("id", eventElement.event.track.id)
      bindUpdateTrackActived.setBool("active", eventElement.event.track.active)
      List(bindUpdateTrackActived)
    }
  }

  private def processTrackDeactivated(eventElement: EventStreamElement[TrackDeactivated]): Future[List[BoundStatement]] = {
    updateTrackActive.map { ps =>
      val bindUpdateTrackDeactivated = ps.bind()
      bindUpdateTrackDeactivated.setUUID("id", eventElement.event.track.id)
      bindUpdateTrackDeactivated.setBool("active", eventElement.event.track.active)
      List(bindUpdateTrackDeactivated)
    }
  }

  private def buildWaypointStatementList(trackstate: TrackState,
                                         insertTrackWaypoint: Future[PreparedStatement],
                                         deleteTrackWaypoints: Future[PreparedStatement]): Future[List[BoundStatement]] = {
    var stmts: List[BoundStatement] = List.empty

    session.select("SELECT id, name, coordinates FROM trackwaypoints WHERE trackid = ?",trackstate.id).map { waypointrow =>
      println(s"########### Returned (${waypointrow.getUUID("id")},${waypointrow.getInt("position")}) to delete statement for track = ${trackstate.id}")
      deleteTrackWaypoint.map { ps =>
        println(s"########### Creating delete statement for track = ${trackstate.id}")
        val bindDeleteTrackWaypoint = ps.bind()
        bindDeleteTrackWaypoint.setUUID("id", waypointrow.getUUID("id"))
        bindDeleteTrackWaypoint.setInt("position", waypointrow.getInt("position"))
        stmts = stmts :+ bindDeleteTrackWaypoint
      }
    }
    var position: Int = 0
    trackstate.waypoints.map{way =>
      insertTrackWaypoint.map { ps =>
        position += 1
        val bindInsertTrackWaypoint = ps.bind()
        bindInsertTrackWaypoint.setUUID("id", way.id)
        bindInsertTrackWaypoint.setUUID("trackid", trackstate.id)
        bindInsertTrackWaypoint.setInt("position", position)
        bindInsertTrackWaypoint.setString("name", way.name)
        bindInsertTrackWaypoint.setString("coordinates", way.coordinates)
        stmts = stmts :+ bindInsertTrackWaypoint
      }
    }
    Future.successful(stmts)
  }

  private def processWaypointAdded(eventElement: EventStreamElement[TrackWayPointAdded]): Future[List[BoundStatement]] = {
    buildWaypointStatementList(eventElement.event.track,insertTrackWaypoint,deleteTrackWaypoint)
  }

  private def processWaypointRemoved(eventElement: EventStreamElement[TrackWayPointRemoved]): Future[List[BoundStatement]] = {
    buildWaypointStatementList(eventElement.event.track,insertTrackWaypoint,deleteTrackWaypoint)
  }

  private def processWaypointMarked(eventElement: EventStreamElement[TrackInitialWayPointMarked]): Future[List[BoundStatement]] = {
    buildWaypointStatementList(eventElement.event.track,insertTrackWaypoint,deleteTrackWaypoint)
  }

  override def buildHandler(): ReadSideProcessor.ReadSideHandler[TrackEvent] = {
    val builder = readSide.builder[TrackEvent]("tracksoffset")
    builder.setGlobalPrepare(createTable)
    builder.setPrepare(tag => prepareTrackStatements())
    builder.setEventHandler[TrackCreated](processTrackCreated)
    builder.setEventHandler[TrackNameChanged](processTrackNameChanged)
    builder.setEventHandler[TrackMaintainerChanged](processTrackMaintainerChanged)
    builder.setEventHandler[TrackActivated](processTrackActivated)
    builder.setEventHandler[TrackDeactivated](processTrackDeactivated)
    builder.setEventHandler[TrackWayPointAdded](processWaypointAdded)
    builder.setEventHandler[TrackWayPointRemoved](processWaypointRemoved)
    builder.setEventHandler[TrackInitialWayPointMarked](processWaypointMarked)
    builder.build()
  }
}

