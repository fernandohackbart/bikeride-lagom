package com.bikeride.ride.impl

import java.util.UUID
import akka.stream.Materializer
import com.bikeride.biker.api.BikerID
import com.bikeride.ride.api
import com.bikeride.ride.api.RideService
import com.bikeride.utils.security.ServerSecurity._
import com.lightbend.lagom.scaladsl.api.transport.{BadRequest, NotFound}
import com.lightbend.lagom.scaladsl.persistence.{PersistentEntityRegistry, ReadSide}
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraSession
import com.lightbend.lagom.scaladsl.server.ServerServiceCall
import scala.concurrent.ExecutionContext


class RideServiceImpl (rideService: RideService,
                        persistentEntityRegistry: PersistentEntityRegistry,
                        session: CassandraSession,
                        readside: ReadSide)
                       (implicit ec: ExecutionContext, mat: Materializer) extends RideService{

  private def refFor(id: UUID) = persistentEntityRegistry.refFor[RideEntity](id.toString)

  override def createRide() = authenticated( userId => ServerServiceCall { req =>
    val rideId = UUID.randomUUID()
    refFor(rideId).ask(CreateRide(RideState(
      rideId,
      req.name,
      BikerID(userId),
      req.limit,
      req.track,
      false,
      false,
      false,
      false,
      Seq.empty[BikerID],
      Seq.empty[BikerID],
      req.opentime,
      req.closetime,
      req.starttime,
      req.finishtime))).map { _ =>
      api.RideID(rideId)
    }
  })

  override def changeRideName(rideID: UUID)= authenticated( userId => ServerServiceCall { req =>
    if (req.name.isEmpty) throw BadRequest("Ride name cannot be empty!")
    else{
      refFor(rideID).ask(ChangeRideName(RideChange(rideID,req.name,req.organizer,req.limit,req.track))).map { _ =>
        api.RideID(rideID)
      }
    }
  })

  override def changeRideOrganizer(rideID: UUID)= authenticated( userId => ServerServiceCall { req =>
    if (req.name.isEmpty) throw BadRequest("Ride organizer cannot be empty!")
    else{
      refFor(rideID).ask(ChangeRideOrganizer(RideChange(rideID,req.name,req.organizer,req.limit,req.track))).map { _ =>
        api.RideID(rideID)
      }
    }
  })

  override def changeRideTrack(rideID: UUID)= authenticated( userId => ServerServiceCall { req =>
    if (req.name.isEmpty) throw BadRequest("Ride track cannot be empty!")
    else{
      refFor(rideID).ask(ChangeRideOrganizer(RideChange(rideID,req.name,req.organizer,req.limit,req.track))).map { _ =>
        api.RideID(rideID)
      }
    }
  })

  override def changeRideLimit(rideID: UUID)= authenticated( userId => ServerServiceCall { req =>
    if (req.name.isEmpty) throw BadRequest("Ride limit cannot be empty!")
    else{
      refFor(rideID).ask(ChangeRideOrganizer(RideChange(rideID,req.name,req.organizer,req.limit,req.track))).map { _ =>
        api.RideID(rideID)
      }
    }
  })

  override def addRideLeader(rideID: UUID)= authenticated( userId => ServerServiceCall { req =>
    refFor(rideID).ask(AddRideLeader(api.RideID(rideID),BikerID(req.bikerID))).map { _ =>
        api.RideID(rideID)
      }
  })

  override def removeRideLeader(rideID: UUID)= authenticated( userId => ServerServiceCall { req =>
    refFor(rideID).ask(RemoveRideLeader(api.RideID(rideID),BikerID(req.bikerID))).map { _ =>
      api.RideID(rideID)
    }
  })

  override def opensubscriptionsRide(rideID: UUID)= authenticated( userId => ServerServiceCall { req =>
    refFor(rideID).ask(OpenSubscriptionsToRide(api.RideID(rideID))).map { _ =>
      api.RideID(rideID)
    }
  })

  override def closesubscriptionsRide(rideID: UUID)= authenticated( userId => ServerServiceCall { req =>
    refFor(rideID).ask(CloseSubscriptionsToRide(api.RideID(rideID))).map { _ =>
      api.RideID(rideID)
    }
  })

  override def subscribetoRide(rideID: UUID)= authenticated( userId => ServerServiceCall { req =>
    refFor(rideID).ask(SubscribeRideRider(api.RideID(rideID),BikerID(req.bikerID))).map { _ =>
      api.RideID(rideID)
    }
  })

  override def unsubscribetoRide(rideID: UUID)= authenticated( userId => ServerServiceCall { req =>
    refFor(rideID).ask(UnsubscribeRideRider(api.RideID(rideID),BikerID(req.bikerID))).map { _ =>
      api.RideID(rideID)
    }
  })

  override def startRide(rideID: UUID)= authenticated( userId => ServerServiceCall { req =>
    refFor(rideID).ask(StartRide(api.RideID(rideID))).map { _ =>
      api.RideID(rideID)
    }
  })

  override def suspendRide(rideID: UUID)= authenticated( userId => ServerServiceCall { req =>
    refFor(rideID).ask(SuspendRide(api.RideID(rideID))).map { _ =>
      api.RideID(rideID)
    }
  })

  override def resumeRide(rideID: UUID)= authenticated( userId => ServerServiceCall { req =>
    refFor(rideID).ask(ResumeRide(api.RideID(rideID))).map { _ =>
      api.RideID(rideID)
    }
  })

  override def finishRide(rideID: UUID)= authenticated( userId => ServerServiceCall { req =>
    refFor(rideID).ask(FinishRide(api.RideID(rideID))).map { _ =>
      api.RideID(rideID)
    }
  })

  override def getRideIsOpen(rideID: UUID) = authenticated( userId => ServerServiceCall { _ =>
    refFor(rideID).ask(GetRide).map {
      case Some(ride) =>
        api.RideIsOpen(rideID,ride.open)
      case None =>
        throw NotFound(s"Ride with id $rideID")
    }
  })

  override def getRideIsStarted(rideID: UUID) = authenticated( userId => ServerServiceCall { _ =>
    refFor(rideID).ask(GetRide).map {
      case Some(ride) =>
        api.RideIsStarted(rideID,ride.started)
      case None =>
        throw NotFound(s"Ride with id $rideID")
    }
  })

  override def getRideIsSuspended(rideID: UUID) = authenticated( userId => ServerServiceCall { _ =>
    refFor(rideID).ask(GetRide).map {
      case Some(ride) =>
        api.RideIsSuspended(rideID,ride.suspended)
      case None =>
        throw NotFound(s"Ride with id $rideID")
    }
  })

  override def getRideIsFinished(rideID: UUID) = authenticated( userId => ServerServiceCall { _ =>
    refFor(rideID).ask(GetRide).map {
      case Some(ride) =>
        api.RideIsFinished(rideID,ride.finished)
      case None =>
        throw NotFound(s"Ride with id $rideID")
    }
  })

  override def getRide(rideID: UUID) = authenticated( userId => ServerServiceCall { _ =>
    refFor(rideID).ask(GetRide).map {
      case Some(ride) =>
        api.Ride(
          api.RideID(rideID),
          api.RideFields(
            ride.name,
            ride.organizer,
            ride.limit,
            ride.track,
            ride.open,
            ride.started,
            ride.suspended,
            ride.finished,
            ride.opentime,
            ride.closetime,
            ride.starttime,
            ride.finishtime),
          ride.leaders,
          ride.riders
        )
      case None =>
        throw NotFound(s"Ride with id $rideID")
    }
  })

}
