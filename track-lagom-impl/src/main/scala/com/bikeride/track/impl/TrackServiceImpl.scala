package com.bikeride.track.impl

import java.util.UUID
import akka.stream.Materializer
import com.bikeride.track.api
import com.bikeride.track.api.TrackService
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.transport.NotFound
import com.lightbend.lagom.scaladsl.persistence.{PersistentEntityRegistry, ReadSide}
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraSession
import scala.concurrent.ExecutionContext

class TrackServiceImpl (trackService: TrackService,
                        persistentEntityRegistry: PersistentEntityRegistry,
                        session: CassandraSession,
                        readside: ReadSide)
                       (implicit ec: ExecutionContext, mat: Materializer) extends TrackService{

  private def refFor(id: UUID) = persistentEntityRegistry.refFor[TrackEntity](id.toString)

  override def createTrack() = ServiceCall { req =>
    val trackId = UUID.randomUUID()
    refFor(trackId).ask(CreateTrack(TrackState(trackId,req.name,req.maintainer,None,req.active))).map { _ =>
      api.TrackID(trackId)
    }
  }

  override def changeTrackName(trackId: UUID) = ServiceCall { req =>
    refFor(trackId).ask(ChangeTrackName(TrackChange(trackId,Some(req.name),Some(req.maintainer)))).map { _ =>
      api.TrackID(trackId)
    }
  }

  override def changeTrackMaintainer(trackId: UUID) = ServiceCall { req =>
    refFor(trackId).ask(ChangeTrackMaintainer(TrackChange(trackId,Some(req.name),Some(req.maintainer)))).map { _ =>
      api.TrackID(trackId)
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
    refFor(trackId).ask(AddTrackWayPoint(trackId,TrackWaypoint(req.id,req.name,req.coordinates))).map { _ =>
      api.TrackID(trackId)
    }
  }

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
        track.waypoints.get.map(way => api.TrackWaypoint(way.id,way.name,way.coordinates))
      case None =>
        throw NotFound(s"Track with id $trackId")
    }
  }

  //override def getTrackLenght(id: UUID): ServiceCall[NotUsed, Integer]

  override def getTrack(trackId: UUID) = ServiceCall { _ =>
    refFor(trackId).ask(GetTrack).map {
      case Some(track) =>
        api.Track(
          api.TrackID(trackId),
          api.TrackFields(track.name,track.maintainer,track.active),
          track.waypoints.get.map(way => api.TrackWaypoint(way.id,way.name,way.coordinates))
        )
      case None =>
        throw NotFound(s"Track with id $trackId")
    }
  }

  //override def getTracks(pageNo: Option[Int], pageSize: Option[Int]): ServiceCall[NotUsed,Seq[Track]]

}