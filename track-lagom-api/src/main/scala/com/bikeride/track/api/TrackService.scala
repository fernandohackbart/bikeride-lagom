package com.bikeride.track.api

import java.util.UUID
import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import play.api.libs.json.{Format, Json}
import scala.collection.Seq


trait TrackService  extends Service {

  def createTrack: ServiceCall[TrackFields, TrackID]
  def changeTrackName(id: UUID): ServiceCall[TrackChangeFields, TrackID]
  def changeTrackMaintainer(id: UUID): ServiceCall[TrackChangeFields, TrackID]
  def activateTrack(id: UUID): ServiceCall[NotUsed, TrackID]
  def deactivateTrack(id: UUID): ServiceCall[NotUsed, TrackID]
  def getTrackIsActive(id: UUID): ServiceCall[NotUsed, TrackIsActive]
  def addTrackWayPoint(id: UUID): ServiceCall[TrackWaypointFields, TrackWaypointID]
  //def addTrackWayPoints(id: UUID): ServiceCall[Seq[TrackWaypoint], TrackID]
  def deleteTrackWayPoint(id: UUID,waypointid: UUID): ServiceCall[NotUsed, TrackID]
  def defineTrackInitialWayPoint(id: UUID,waypointid: UUID): ServiceCall[NotUsed, TrackID]
  def getTrackWayPoints(id: UUID): ServiceCall[NotUsed, Seq[TrackWaypoint]]
  //def getTrackLenght(id: UUID): ServiceCall[NotUsed, Integer]
  def getTrack(id: UUID): ServiceCall[NotUsed, Track]
  def getTracks(pageNo: Option[Int], pageSize: Option[Int]): ServiceCall[NotUsed,Seq[Track]]
  //def getTrackWayPoints(pageNo: Option[Int], pageSize: Option[Int]): ServiceCall[NotUsed,Seq[TrackWaypoint]]

  override final def descriptor = {
    import Service._
    named("track")
      .withCalls(
        restCall(Method.POST,"/api/track", createTrack),
        restCall(Method.PUT,"/api/track/:id/name", changeTrackName _),
        restCall(Method.PUT,"/api/track/:id/maintainer", changeTrackMaintainer _),
        restCall(Method.POST,"/api/track/:id/activate", activateTrack _),
        restCall(Method.POST,"/api/track/:id/deactivate", deactivateTrack _),
        restCall(Method.POST,"/api/track/:id/waypoint", addTrackWayPoint _),
        //restCall(Method.POST,"/api/track/:id/waypoints", addTrackWayPoints _),
        //restCall(Method.GET,"/api/track/:id/waypoints", getTrackWayPoints _),
        restCall(Method.DELETE,"/api/track/:id/waypoint/:waypointid", deleteTrackWayPoint _),
        restCall(Method.POST,"/api/track/:id/waypoint/:waypointid/initial", defineTrackInitialWayPoint _),
        restCall(Method.GET,"/api/track/:id/waypoints", getTrackWayPoints _),
        //restCall(Method.GET,"/api/track/:id/lenght", getTrackLenght _),
        restCall(Method.GET,"/api/track/:id/isactive", getTrackIsActive _),
        restCall(Method.GET,"/api/track/:id", getTrack _),
        restCall(Method.GET,"/api/tracks?pageNo&pageSize", getTracks _)
      ).withAutoAcl(true)
  }
}

case class TrackID(id: UUID)
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