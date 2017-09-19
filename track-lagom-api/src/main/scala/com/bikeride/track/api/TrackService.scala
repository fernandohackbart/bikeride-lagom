package com.bikeride.track.api

import java.util.UUID
import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import play.api.libs.json.{Format, Json}
import scala.collection.Seq
import com.bikeride.utils.security.SecurityHeaderFilter

trait TrackService  extends Service {

  def createTrack: ServiceCall[TrackFields, TrackID]
  def changeTrackName(trackID: UUID): ServiceCall[TrackChangeFields, TrackID]
  def changeTrackMaintainer(trackID: UUID): ServiceCall[TrackChangeFields, TrackID]
  def activateTrack(trackID: UUID): ServiceCall[NotUsed, TrackID]
  def deactivateTrack(trackID: UUID): ServiceCall[NotUsed, TrackID]
  def getTrackIsActive(trackID: UUID): ServiceCall[NotUsed, TrackIsActive]
  def addTrackWayPoint(trackID: UUID): ServiceCall[TrackWaypointFields, TrackWaypointID]
  //def addTrackWayPoints(trackID: UUID): ServiceCall[Seq[TrackWaypoint], TrackID]
  def deleteTrackWayPoint(trackID: UUID,waypointid: UUID): ServiceCall[NotUsed, TrackID]
  def defineTrackInitialWayPoint(trackID: UUID,waypointid: UUID): ServiceCall[NotUsed, TrackID]
  def getTrackWayPoints(trackID: UUID): ServiceCall[NotUsed, Seq[TrackWaypoint]]
  //def getTrackLenght(trackID: UUID): ServiceCall[NotUsed, Integer]
  def getTrack(trackID: UUID): ServiceCall[NotUsed, Track]
  def getTracks(pageNo: Option[Int], pageSize: Option[Int]): ServiceCall[NotUsed,Seq[Track]]
  def readTrackWayPoints(trackID: UUID): ServiceCall[NotUsed,Seq[TrackWaypoint]]

  override final def descriptor = {
    import Service._
    named("track").withCalls(
        restCall(Method.POST,"/api/track", createTrack),
        restCall(Method.PUT,"/api/track/:trackID/name", changeTrackName _),
        restCall(Method.PUT,"/api/track/:trackID/maintainer", changeTrackMaintainer _),
        restCall(Method.POST,"/api/track/:trackID/activate", activateTrack _),
        restCall(Method.POST,"/api/track/:trackID/deactivate", deactivateTrack _),
        restCall(Method.GET,"/api/track/:trackID/isactive", getTrackIsActive _),
        restCall(Method.POST,"/api/track/:trackID/waypoint", addTrackWayPoint _),
        //restCall(Method.POST,"/api/track/:trackID/waypoints", addTrackWayPoints _),
        restCall(Method.DELETE,"/api/track/:trackID/waypoint/:waypointid", deleteTrackWayPoint _),
        restCall(Method.POST,"/api/track/:trackID/waypoint/:waypointid/initial", defineTrackInitialWayPoint _),
        restCall(Method.GET,"/api/track/:trackID/waypoints", getTrackWayPoints _),
        //restCall(Method.GET,"/api/track/:trackID/lenght", getTrackLenght _),
        restCall(Method.GET,"/api/track/:trackID", getTrack _),
        restCall(Method.GET,"/api/tracks?pageNo&pageSize", getTracks _),
        restCall(Method.GET,"/api/track/:trackID/readwaypoints", readTrackWayPoints _)
      ).withHeaderFilter(SecurityHeaderFilter.Composed)
  }
}

case class TrackID(trackID: UUID)
object  TrackID {
  implicit val format: Format[TrackID] = Json.format
}

case class TrackFields(name: String,
                       maintainer: UUID,
                       active: Boolean = true)
object  TrackFields {
  implicit val format: Format[TrackFields] = Json.format
}

case class TrackChangeFields(name: Option[String] = None,
                             maintainer: Option[UUID] = None)
object  TrackChangeFields {
  implicit val format: Format[TrackChangeFields] = Json.format
}

case class TrackWaypointFields(name: String, coordinates: String)
object  TrackWaypointFields {
  implicit val format: Format[TrackWaypointFields] = Json.format
}

case class TrackWaypointID(trackid: UUID, trackwaypointid: UUID)
object  TrackWaypointID {
  implicit val format: Format[TrackWaypointID] = Json.format
}

case class TrackWaypoint(trackwaypointid: TrackWaypointID, trackWaypointFields: TrackWaypointFields)
object  TrackWaypoint {
  implicit val format: Format[TrackWaypoint] = Json.format
}

case class TrackIsActive(id: UUID, active: Boolean)
object  TrackIsActive {
  implicit val format: Format[TrackIsActive] = Json.format
}

case class Track(trackID: TrackID, trackFields: TrackFields, trackWaypoints: Seq[TrackWaypoint])
object  Track {
  implicit val format: Format[Track] = Json.format
}