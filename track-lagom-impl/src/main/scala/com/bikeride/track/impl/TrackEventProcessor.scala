package com.bikeride.track.impl

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
      "coordinates TEXT" +
      "PRIMARY KEY (id))")
  }

  private val insertTrackPromise = Promise[PreparedStatement]
  private def insertTrack: Future[PreparedStatement] = insertTrackPromise.future

  private val updateTrackNamePromise = Promise[PreparedStatement]
  private def updateTrackName: Future[PreparedStatement] = updateTrackNamePromise.future

  private val updateTrackMaintainerPromise = Promise[PreparedStatement]
  private def updateTrackMaintainer: Future[PreparedStatement] = updateTrackMaintainerPromise.future

  private val updateTrackActivePromise = Promise[PreparedStatement]
  private def updateTrackActive: Future[PreparedStatement] = updateTrackActivePromise.future

  private val deleteTrackWaypointsPromise = Promise[PreparedStatement]
  private def deleteTrackWaypoints: Future[PreparedStatement] = deleteTrackWaypointsPromise.future

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

    val deleteWaypoints = session.prepare("DELETE FROM  trackwaypoints where trackid = ?")
    deleteTrackWaypointsPromise.completeWith(deleteWaypoints)
    deleteWaypoints.map(_ => Done)

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
      val bindUpdateTrackActive = ps.bind()
      bindUpdateTrackActive.setUUID("id", eventElement.event.track.id)
      List(bindUpdateTrackActive)
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

/*
  private def processWaypointAdded(eventElement: EventStreamElement[TrackWayPointAdded]): Future[List[BoundStatement]] = {

    //TODO execute many statements in one processor
    Future.successful(
      List(
        deleteTrackWaypoints.map { ps =>
          val bindDeleteTrackWaypoints = ps.bind()
          bindDeleteTrackWaypoints.setUUID("id", eventElement.event.track.id)
          bindDeleteTrackWaypoints.setBool("active", eventElement.event.track.active)
        }
      )
    )


    deleteTrackWaypoints.map { ps =>
      val bindDeleteTrackWaypoints = ps.bind()
      bindDeleteTrackWaypoints.setUUID("id", eventElement.event.track.id)
      bindDeleteTrackWaypoints.setBool("active", eventElement.event.track.active)
      List(bindDeleteTrackWaypoints)
    }
    //TODO loop over the waypoints and insert with right position
    eventElement.event.track.waypoints.get.foreach { waypoint =>
      insertTrackWaypoint.map { ps =>
        val bindInsertTrackWaypoint = ps.bind()
        bindInsertTrackWaypoint.setUUID("id", waypoint.id)
        bindInsertTrackWaypoint.setUUID("trackid", eventElement.event.track.id)
        bindInsertTrackWaypoint.setInt("position",0)
        bindInsertTrackWaypoint.setString("name", waypoint.name)
        bindInsertTrackWaypoint.setString("coordinates", waypoint.coordinates)
        List(bindInsertTrackWaypoint)
      }
    }

  }
*/

  override def buildHandler(): ReadSideProcessor.ReadSideHandler[TrackEvent] = {
    val builder = readSide.builder[TrackEvent]("tracksoffset")
    builder.setGlobalPrepare(createTable)
    builder.setPrepare(tag => prepareTrackStatements())
    builder.setEventHandler[TrackCreated](processTrackCreated)
    builder.setEventHandler[TrackNameChanged](processTrackNameChanged)
    builder.setEventHandler[TrackMaintainerChanged](processTrackMaintainerChanged)
    builder.setEventHandler[TrackActivated](processTrackActivated)
    builder.setEventHandler[TrackDeactivated](processTrackDeactivated)
    //builder.setEventHandler[TrackWayPointAdded](processWaypointAdded)
    //builder.setEventHandler[TrackWayPointRemoved](processWaypoinstChanged)
    //builder.setEventHandler[TrackInitialWayPointMarked](processWaypoinstChanged)
    builder.build()
  }
}