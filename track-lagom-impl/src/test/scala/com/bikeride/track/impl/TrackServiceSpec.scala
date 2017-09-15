package com.bikeride.track.impl

import java.util.UUID

import com.lightbend.lagom.scaladsl.server.LocalServiceLocator
import com.lightbend.lagom.scaladsl.testkit.ServiceTest
import org.scalatest.{AsyncWordSpec, BeforeAndAfterAll, Matchers}
import com.bikeride.track.api
import com.lightbend.lagom.scaladsl.api.transport.BadRequest

class TrackServiceSpec extends AsyncWordSpec with Matchers with BeforeAndAfterAll {

  private val server = ServiceTest.startServer(
    ServiceTest.defaultSetup
      .withCassandra(true)
  ) { ctx =>
    new TrackApplication(ctx) with LocalServiceLocator
  }
  private val client = server.serviceClient.implement[api.TrackService]

  private val maintainerID: UUID = UUID.randomUUID()
  private val changedMaintainerID: UUID = UUID.randomUUID()
  private val trackFields = api.TrackFields("Some track name",maintainerID,true)
  private var trackID: api.TrackID = api.TrackID(UUID.randomUUID())
  private var track = api.Track(trackID,trackFields,Seq.empty[api.TrackWaypoint])
  private var waypoint1ID = api.TrackWaypointID(trackID.id,UUID.randomUUID())
  private val waypoint1Fields = api.TrackWaypointFields("waypoint1","waypoint1")
  private var waypoint1 = api.TrackWaypoint(waypoint1ID,waypoint1Fields)
  private var waypoint2ID = api.TrackWaypointID(trackID.id,UUID.randomUUID())
  private val waypoint2Fields = api.TrackWaypointFields("waypoint2","waypoint2")
  private var waypoint2 = api.TrackWaypoint(waypoint2ID,waypoint2Fields)
  private var waypoint3ID = api.TrackWaypointID(trackID.id,UUID.randomUUID())
  private val waypoint3Fields = api.TrackWaypointFields("waypoint3","waypoint3")
  private var waypoint3 = api.TrackWaypoint(waypoint3ID,waypoint3Fields)
  private var waypoint4ID = api.TrackWaypointID(trackID.id,UUID.randomUUID())
  private val waypoint4Fields = api.TrackWaypointFields("waypoint4","waypoint4")
  private var waypoint4 = api.TrackWaypoint(waypoint4ID,waypoint4Fields)

  "biker-lagom service" should {
    "create biker" in {
      client.createTrack.invoke(trackFields).map { answer =>
        trackID = answer
        answer shouldBe a [api.TrackID]
      }
    }

    "get biker by ID" in {
      client.getTrack(trackID.id).invoke.map { answer =>
        track = api.Track(trackID,trackFields,Seq.empty[api.TrackWaypoint])
        answer should equal (track)
      }
    }

    "fail with empty name on change track name" in {
      client.changeTrackName(trackID.id).invoke(api.TrackChangeFields(None,None)).map { answer =>
        fail("Emtpy name should raise an error!")
      }.recoverWith {
        case (err: BadRequest) =>
          err.exceptionMessage.detail should be("Track name cannot be empty!")
      }
    }

    "change track name" in {
      client.changeTrackName(trackID.id).invoke(api.TrackChangeFields(Some("Some track name changed!"),None)).map { answer =>
        answer.id should equal (trackID.id)
      }
    }

    "get track changed name" in {
      client.getTrack(trackID.id).invoke.map { answer =>
        answer.trackFields.name should equal ("Some track name changed!")
      }
    }

    "change track maintainer" in {
      client.changeTrackMaintainer(trackID.id).invoke(api.TrackChangeFields(None,Some(changedMaintainerID))).map { answer =>
        answer.id should equal (trackID.id)
      }
    }

    "get track changed maintainer" in {
      client.getTrack(trackID.id).invoke.map { answer =>
        answer.trackFields.maintainer should equal (changedMaintainerID)
      }
    }

    "deactivate track" in {
      client.deactivateTrack(trackID.id).invoke().map { answer =>
        answer.id should equal (trackID.id)
      }
    }

    "find track deactivated" in {
      client.getTrackIsActive(trackID.id).invoke.map { answer =>
        answer.active should equal (false)
      }
    }

    "activate track" in {
      client.activateTrack(trackID.id).invoke().map { answer =>
        answer.id should equal (trackID.id)
      }
    }

    "find track activated" in {
      client.getTrackIsActive(trackID.id).invoke.map { answer =>
        answer.active should equal (true)
      }
    }

    "add waypoint (waypoint1,waypoint1) to track" in {
      client.addTrackWayPoint(trackID.id).invoke(waypoint1Fields).map { answer =>
        waypoint1ID = answer
        waypoint1 = api.TrackWaypoint(answer,waypoint1Fields)
        answer shouldBe a [api.TrackWaypointID]
      }
    }

    "add waypoint (waypoint2,waypoint2) to track" in {
      client.addTrackWayPoint(trackID.id).invoke(waypoint2Fields).map { answer =>
        waypoint2ID = answer
        waypoint2 = api.TrackWaypoint(answer,waypoint2Fields)
        answer shouldBe a [api.TrackWaypointID]
      }
    }

    "add waypoint (waypoint3,waypoint3) to track" in {
      client.addTrackWayPoint(trackID.id).invoke(waypoint3Fields).map { answer =>
        waypoint3ID = answer
        waypoint3 = api.TrackWaypoint(answer,waypoint3Fields)
        answer shouldBe a [api.TrackWaypointID]
      }
    }

    "define waypoint (waypoint3,waypoint3) as initial" in {
      client.defineTrackInitialWayPoint(trackID.id,waypoint3ID.trackwaypointid).invoke.map { answer =>
        answer should be (trackID)
      }
    }

    "waypoint (waypoint3,waypoint3) should be initial" in {
      client.getTrackWayPoints(trackID.id).invoke.map { answer =>
        answer.seq.indexOf(waypoint3) should be (0)
      }
    }

    "waypoint (waypoint1,waypoint1) should be second" in {
      client.getTrackWayPoints(trackID.id).invoke.map { answer =>
        answer.seq.indexOf(waypoint1) should be (1)
      }
    }

    "waypoint (waypoint2,waypoint2) should be third" in {
      client.getTrackWayPoints(trackID.id).invoke.map { answer =>
        answer.seq.indexOf(waypoint2) should be (2)
      }
    }

    "add waypoint (waypoint4,waypoint4) to track" in {
      client.addTrackWayPoint(trackID.id).invoke(waypoint4Fields).map { answer =>
        waypoint4ID = answer
        waypoint4 = api.TrackWaypoint(answer,waypoint4Fields)
        answer shouldBe a [api.TrackWaypointID]
      }
    }

    "define waypoint (waypoint2,waypoint2) as initial" in {
      client.defineTrackInitialWayPoint(trackID.id,waypoint2ID.trackwaypointid).invoke.map { answer =>
        answer should be (trackID)
      }
    }

    "waypoint (waypoint2,waypoint2) should be initial" in {
      client.getTrackWayPoints(trackID.id).invoke.map { answer =>
        answer.seq.indexOf(waypoint2) should be (0)
      }
    }

    "waypoint (waypoint4,waypoint4) should be second" in {
      client.getTrackWayPoints(trackID.id).invoke.map { answer =>
        answer.seq.indexOf(waypoint4) should be (1)
      }
    }

    "waypoint (waypoint3,waypoint3) should be third" in {
      client.getTrackWayPoints(trackID.id).invoke.map { answer =>
        answer.seq.indexOf(waypoint3) should be (2)
      }
    }

    "waypoint (waypoint1,waypoint1) should be fourth" in {
      client.getTrackWayPoints(trackID.id).invoke.map { answer =>
        answer.seq.indexOf(waypoint1) should be (3)
      }
    }

    "remove waypoint (waypoint3,waypoint3)" in {
      client.deleteTrackWayPoint(waypoint3.trackwaypointid.trackid,waypoint3.trackwaypointid.trackwaypointid).invoke.map { answer =>
        answer shouldBe a [api.TrackID]
      }
    }

    "waypoint (waypoint2,waypoint2) should be initial after remove waypoint3" in {
      client.getTrackWayPoints(trackID.id).invoke.map { answer =>
        answer.seq.indexOf(waypoint2) should be (0)
      }
    }

    "waypoint (waypoint4,waypoint4) should be second after remove waypoint3" in {
      client.getTrackWayPoints(trackID.id).invoke.map { answer =>
        answer.seq.indexOf(waypoint4) should be (1)
      }
    }

    //TODO check why the remove of one item makes the rest of items to keep their original indexOf
    "waypoint (waypoint1,waypoint1) should be third after remove waypoint3" in {
      client.getTrackWayPoints(trackID.id).invoke.map { answer =>
        answer.seq.indexOf(waypoint1) should be (2)
      }
    }

    //TODO test the read side response (even when empty)

    //"find track in tracks sequence from the read side" in {
    //  client.getTracks(Some(1),Some(10)).invoke.map { answer =>
    //    answer.seq.filter(track => (track.trackID.id==trackID.id)).last.trackID.id should === (trackID.id)
    //  }
    //}
  }

  override protected def afterAll() = server.stop()
}