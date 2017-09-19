package com.bikeride.ride.api

import java.util.UUID
import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import play.api.libs.json.{Format, Json}
import scala.collection.Seq
import com.bikeride.biker.api.BikerID
import com.bikeride.track.api.TrackID
import com.bikeride.utils.security.SecurityHeaderFilter
import java.time.Instant

trait RideService extends Service {

  def createRide: ServiceCall[RideFields, RideID]
  def changeRideName(rideID: UUID): ServiceCall[RideChangeFields, RideID]
  def changeRideOrganizer(rideID: UUID): ServiceCall[RideChangeFields, RideID]
  def changeRideTrack(rideID: UUID): ServiceCall[RideChangeFields, RideID]
  def changeRideLimit(rideID: UUID): ServiceCall[RideChangeFields, RideID]
  def addRideLeader(rideID: UUID): ServiceCall[BikerID, RideID]
  def removeRideLeader(rideID: UUID): ServiceCall[BikerID, RideID]
  def opensubscriptionsRide(rideID: UUID): ServiceCall[NotUsed, RideID]
  def closesubscriptionsRide(rideID: UUID): ServiceCall[NotUsed, RideID]
  def subscribetoRide(rideID: UUID): ServiceCall[BikerID, RideID]
  def unsubscribetoRide(rideID: UUID): ServiceCall[BikerID, RideID]
  def startRide(rideID: UUID): ServiceCall[NotUsed, RideID]
  def finishRide(rideID: UUID): ServiceCall[NotUsed, RideID]
  def getRideIsOpen(rideID: UUID): ServiceCall[NotUsed, RideIsOpen]
  def getRideIsClosed(rideID: UUID): ServiceCall[NotUsed, RideIsClosed]
  def getRideIsStarted(rideID: UUID): ServiceCall[NotUsed, RideIsStarted]
  def getRideIsFinished(rideID: UUID): ServiceCall[NotUsed, RideIsFinished]
  def getRide(rideID: UUID): ServiceCall[NotUsed, Ride]
  def getRides(pageNo: Option[Int], pageSize: Option[Int]): ServiceCall[NotUsed,Seq[Ride]]

  override final def descriptor = {
    import Service._
    named("ride")
      .withCalls(
        restCall(Method.POST,"/api/ride", createRide),
        restCall(Method.PUT,"/api/ride/:rideID/name", changeRideName _),
        restCall(Method.PUT,"/api/ride/:rideID/organizer", changeRideOrganizer _),
        restCall(Method.PUT,"/api/ride/:rideID/track", changeRideTrack _),
        restCall(Method.PUT,"/api/ride/:rideID/limit", changeRideLimit _),
        restCall(Method.PUT,"/api/ride/:rideID/addleader", addRideLeader _),
        restCall(Method.PUT,"/api/ride/:rideID/removeleader", removeRideLeader _),
        restCall(Method.POST,"/api/ride/:rideID/opensubscriptions", opensubscriptionsRide _),
        restCall(Method.POST,"/api/ride/:rideID/closesubscriptions", closesubscriptionsRide _),
        restCall(Method.POST,"/api/ride/:rideID/subscribe", subscribetoRide _),
        restCall(Method.POST,"/api/ride/:rideID/unsubscribe", unsubscribetoRide _),
        restCall(Method.POST,"/api/ride/:rideID/start", startRide _),
        restCall(Method.POST,"/api/ride/:rideID/finish", finishRide _),
        restCall(Method.GET,"/api/ride/:rideID/isOpen", getRideIsOpen _),
        restCall(Method.GET,"/api/ride/:rideID/isClosed", getRideIsClosed _),
        restCall(Method.GET,"/api/ride/:rideID/isStarted", getRideIsStarted _),
        restCall(Method.GET,"/api/ride/:rideID/isFinished", getRideIsFinished _),
        restCall(Method.GET,"/api/ride/:rideID", getRide _),
        restCall(Method.GET,"/api/rides?pageNo&pageSize", getRides _)
      ).withHeaderFilter(SecurityHeaderFilter.Composed)
  }
}

case class RideID(rideID: UUID)
object  RideID {
  implicit val format: Format[RideID] = Json.format
}

case class RideFields(name: String,
                      organizer: BikerID,
                      limit: Int,
                      opentime: Option[Instant] = None,
                      closetime: Option[Instant] = None,
                      starttime: Option[Instant] = None,
                      finishtime: Option[Instant] = None,
                      track: TrackID)
object  RideFields {
  implicit val format: Format[RideFields] = Json.format
}

case class RideChangeFields(name: Option[String] = None,
                            organizer: Option[BikerID] = None,
                            limit: Option[Int] = None,
                            opentime: Option[Instant] = None,
                            closetime: Option[Instant] = None,
                            starttime: Option[Instant] = None,
                            finishtime: Option[Instant] = None,
                            track: Option[TrackID] = None)
object  RideChangeFields {
  implicit val format: Format[RideChangeFields] = Json.format
}

case class Ride(rideID: RideID, rideFields: RideFields,leaders: Seq[BikerID],participants: Seq[BikerID])
object  Ride {
  implicit val format: Format[Ride] = Json.format
}

case class RideIsOpen(rideID: UUID, open: Boolean)
object  RideIsOpen {
  implicit val format: Format[RideIsOpen] = Json.format
}

case class RideIsClosed(rideID: UUID, closed: Boolean)
object  RideIsClosed {
  implicit val format: Format[RideIsClosed] = Json.format
}

case class RideIsStarted(rideID: UUID, started: Boolean)
object  RideIsStarted {
  implicit val format: Format[RideIsStarted] = Json.format
}

case class RideIsFinished(rideID: UUID, finished: Boolean)
object  RideIsFinished {
  implicit val format: Format[RideIsFinished] = Json.format
}
