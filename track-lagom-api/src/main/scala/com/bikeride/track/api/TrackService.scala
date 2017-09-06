package com.bikeride.track.api

import java.util.UUID
import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import play.api.libs.json.{Format, Json}
import scala.collection.Seq

trait TrackService  extends Service {
  def createTrack: ServiceCall[TrackFields, TrackID]
  def changeTrackName(id: UUID): ServiceCall[TrackFields, TrackID]
  def changeBikerMaintainer(id: UUID): ServiceCall[TrackFields, TrackID]
  def activateTrack(id: UUID): ServiceCall[NotUsed, TrackID]
  def deactivateTrack(id: UUID): ServiceCall[NotUsed, TrackID]
  def getTrackIsActive(id: UUID): ServiceCall[NotUsed, TrackIsActive]
  def deleteTrackWayPoint(id: UUID,waypointid: UUID): ServiceCall[NotUsed, TrackID]
  def defineTrackInitialWayPoint(id: UUID,waypointid: UUID): ServiceCall[NotUsed, TrackID]
  def getTrackWayPoints(id: UUID): ServiceCall[NotUsed, Seq[TrackWaypoint]]
  def getTrackLenght(id: UUID): ServiceCall[NotUsed, Integer]
  def getTrack(id: UUID): ServiceCall[NotUsed, Track]
  def getTracks(pageNo: Option[Int], pageSize: Option[Int]): ServiceCall[NotUsed,Seq[Track]]

  override final def descriptor = {
    import Service._
    named("biker")
      .withCalls(
        restCall(Method.POST,"/api/biker", createTrack),
        restCall(Method.PUT,"/api/biker/:id/name", changeTrackName _),
        restCall(Method.PUT,"/api/biker/:id/maintainer", changeBikerMaintainer _),
        restCall(Method.POST,"/api/biker/:id/activate", activateTrack _),
        restCall(Method.POST,"/api/biker/:id/deactivate", deactivateTrack _),
        restCall(Method.DELETE,"/api/track/:id/waypoint/:waypointid", deleteTrackWayPoint _),
        restCall(Method.POST,"/api/track/:id/waypoint/:waypointid/initial", defineTrackInitialWayPoint _),
        restCall(Method.GET,"/api/track/:id/waypoints", getTrackWayPoints _),
        restCall(Method.GET,"/api/track/:id/lenght", getTrackLenght _),
        restCall(Method.GET,"/api/biker/:id/isactive", getTrackIsActive _),
        restCall(Method.GET,"/api/biker/:id", getTrack _),
        restCall(Method.GET,"/api/bikers?pageNo&pageSize", getTracks _)
      ).withAutoAcl(true)
  }
}

case class TrackID(id: UUID)
object  TrackID {
  implicit val format: Format[TrackID] = Json.format
}

case class TrackFields(name: String,
                       maintainer: Option[String] = None,
                       active: Option[Boolean] = Some(true))
object  TrackFields {
  implicit val format: Format[TrackFields] = Json.format
}

case class TrackChangeFields(name: Option[String] = None,
                             maintainer: Option[String] = None
object  TrackChangeFields {
  implicit val format: Format[TrackChangeFields] = Json.format
}

case class TrackWaypoint(id: UUID, name: String, coordinates: String)
object  TrackWaypoint {
  implicit val format: Format[TrackWaypoint] = Json.format
}

case class TrackIsActive(id: UUID, active: Boolean)
object  TrackIsActive {
  implicit val format: Format[TrackIsActive] = Json.format
}

case class Track(bikerID: TrackID, bikerFields: TrackFields, waypoints: Seq[TrackWaypoint])
object  Track {
  implicit val format: Format[Track] = Json.format
}