package com.bikeride.track.impl

import java.util.UUID
import akka.NotUsed
import akka.stream.Materializer
import com.bikeride.track.api
import com.bikeride.track.api.TrackService
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.transport.{BadRequest, NotFound}
import com.lightbend.lagom.scaladsl.persistence.{PersistentEntityRegistry, ReadSide}
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraSession
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._

class TrackServiceImpl (trackService: TrackService,
                        persistentEntityRegistry: PersistentEntityRegistry,
                        session: CassandraSession,
                        readside: ReadSide)
                       (implicit ec: ExecutionContext, mat: Materializer) extends TrackService{

  private def refFor(id: UUID) = persistentEntityRegistry.refFor[TrackEntity](id.toString)

  override def createTrack() = ServiceCall { req =>
    val trackId = UUID.randomUUID()
    refFor(trackId).ask(CreateTrack(TrackState(trackId,req.name,req.maintainer,Seq.empty[TrackWaypoint],req.active))).map { _ =>
      api.TrackID(trackId)
    }
  }

  override def changeTrackName(trackId: UUID) = ServiceCall { req =>
    if (req.name.isEmpty) throw BadRequest("Track name cannot be empty!")
    else{
      refFor(trackId).ask(ChangeTrackName(TrackChange(trackId,req.name,req.maintainer))).map { _ =>
        api.TrackID(trackId)
      }
    }
  }

  override def changeTrackMaintainer(trackId: UUID) = ServiceCall { req =>
    if (req.maintainer.isEmpty) throw BadRequest("Track maintainer cannot be empty!")
    else{
      refFor(trackId).ask(ChangeTrackMaintainer(TrackChange(trackId,req.name,req.maintainer))).map { _ =>
        api.TrackID(trackId)
      }
    }
  }

  override def activateTrack(trackId: UUID) = ServiceCall { req =>
    refFor(trackId).ask(ActivateTrack(trackId)).map { _ =>
      api.TrackID(trackId)
    }
  }

  override def deactivateTrack(trackId: UUID) = ServiceCall { req =>
    refFor(trackId).ask(DeactivateTrack(trackId)).map { _ =>
      api.TrackID(trackId)
    }
  }

  override def getTrackIsActive(trackId: UUID) = ServiceCall { _ =>
    refFor(trackId).ask(GetTrack).map {
      case Some(track) =>
        api.TrackIsActive(trackId,track.active)
      case None =>
        throw NotFound(s"Track with id $trackId")
    }
  }

  override def addTrackWayPoint(trackId: UUID) = ServiceCall { req =>
    val trackwaypointid = UUID.randomUUID()
    refFor(trackId).ask(AddTrackWayPoint(trackId,TrackWaypoint(trackwaypointid,req.name,req.coordinates))).map { _ =>
      api.TrackWaypointID(trackId,trackwaypointid)
    }
  }

  //TODO implement addTrackWayPoints
  //override def addTrackWayPoints(id: UUID): ServiceCall[Seq[TrackWaypoint], TrackID]

  override def deleteTrackWayPoint(trackId: UUID,waypointID: UUID) = ServiceCall { req =>
    refFor(trackId).ask(RemoveTrackWayPoint(trackId,waypointID)).map { _ =>
      api.TrackID(trackId)
    }
  }

  override def defineTrackInitialWayPoint(trackId: UUID,waypointID: UUID) = ServiceCall { req =>
    refFor(trackId).ask(MarkWayPointInitial(trackId,waypointID)).map { _ =>
      api.TrackID(trackId)
    }
  }

  override def getTrackWayPoints(trackId: UUID) = ServiceCall { _ =>
    refFor(trackId).ask(GetTrack).map {
      case Some(track) =>
        track.waypoints.map(way => api.TrackWaypoint(api.TrackWaypointID(trackId,way.id),api.TrackWaypointFields(way.name,way.coordinates)))
      case None =>
        throw NotFound(s"Track with id $trackId")
    }
  }

  //TODO implement the getTrackLenght
  //override def getTrackLenght(id: UUID): ServiceCall[NotUsed, Integer]

  override def getTrack(trackID: UUID) = ServiceCall { _ =>
    refFor(trackID).ask(GetTrack).map {
      case Some(track) =>

        var waypoints = Seq.empty[api.TrackWaypoint]
        if (!track.waypoints.isEmpty){
          waypoints = track.waypoints.map(way => api.TrackWaypoint(api.TrackWaypointID(trackID,way.id),api.TrackWaypointFields(way.name,way.coordinates)))
        }
        api.Track(
          api.TrackID(trackID),
          api.TrackFields(track.name,track.maintainer,track.active),
          waypoints
        )
      case None =>
        throw NotFound(s"Track with id $trackID")
    }
  }

  private val DefaultPageSize = 10
  override def getTracks( pageNo: Option[Int], pageSize: Option[Int]) = ServiceCall[NotUsed, Seq[api.Track]] { req =>
    println(s"getTracks(${pageNo.getOrElse(0)}, ${pageSize.getOrElse(DefaultPageSize)})   ##############")
    session.select("SELECT id, name, maintainer, active FROM tracks LIMIT ?",Integer.valueOf(pageSize.getOrElse(DefaultPageSize))).map { row =>
      api.Track(
        api.TrackID(row.getUUID("id")),
        api.TrackFields(
          row.getString("name"),
          row.getUUID("maintainer"),
          row.getBool("active")
        ),
        Await.result(session.select("SELECT id, name, coordinates FROM trackwaypoints WHERE trackid = ? ALLOW FILTERING ",row.getUUID("id")).map { waypointrow =>
          api.TrackWaypoint(
            api.TrackWaypointID(UUID.randomUUID(),waypointrow.getUUID("id")),
            api.TrackWaypointFields(waypointrow.getString("name"),waypointrow.getString("coordinates"))
          )
        }.runFold(Seq.empty[api.TrackWaypoint])((acc, e) => acc :+ e),30 seconds)
      )
    }.runFold(Seq.empty[api.Track])((acc, e) => acc :+ e)
  }

  //TODO implement the readTrackWayPoints
  override def readTrackWayPoints(trackID: UUID) = ServiceCall[NotUsed, Seq[api.TrackWaypoint]] { req =>
    session.select("SELECT id, name, coordinates FROM trackwaypoints WHERE trackid = ? ALLOW FILTERING ",trackID).map { waypointrow =>
      api.TrackWaypoint(
      api.TrackWaypointID(UUID.randomUUID(),waypointrow.getUUID("id")),
      api.TrackWaypointFields(waypointrow.getString("name"),waypointrow.getString("coordinates"))
      )
    }.runFold(Seq.empty[api.TrackWaypoint])((acc, e) => acc :+ e)
  }
}

