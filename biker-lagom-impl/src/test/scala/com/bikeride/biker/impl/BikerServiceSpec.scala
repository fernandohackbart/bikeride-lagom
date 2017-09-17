package com.bikeride.biker.impl

import java.util.UUID

import com.lightbend.lagom.scaladsl.server.LocalServiceLocator
import com.lightbend.lagom.scaladsl.testkit.ServiceTest
import org.scalatest.{AsyncWordSpec, BeforeAndAfterAll, Matchers}
import com.bikeride.biker.api._
import com.bikeride.utils.security.Token
import com.lightbend.lagom.scaladsl.api.transport.BadRequest

class BikerServiceSpec extends AsyncWordSpec with Matchers with BeforeAndAfterAll {

  private val server = ServiceTest.startServer(
    ServiceTest.defaultSetup
      .withCassandra(true)
  ) { ctx =>
    new BikerApplication(ctx) with LocalServiceLocator
  }
  private val client = server.serviceClient.implement[BikerService]

  private val bikerFields = BikerFields("Some biker name",Some("AvatarB64"),Some("Some blood type"),Some("Some mobile"),Some("Some email"),true)
  private val bikerClientID = BikerClient(UUID.randomUUID())
  private var bikerID: BikerID = BikerID(UUID.randomUUID())
  private var bikerToken = BikerToken(bikerID,Token("dummy",Some("false")))
  private var biker = Biker(bikerID,bikerFields)

  "biker-lagom service" should {
    "create biker" in {
      client.createBiker.invoke(BikerCreateRequest(bikerClientID,bikerFields)).map { answer =>
        bikerToken = answer
        bikerID = bikerToken.bikerID
        answer shouldBe a [BikerToken]
      }
    }

    "get biker by ID" in {
      client.getBiker(bikerID.bikerID).invoke.map { answer =>
        biker = Biker(bikerID,bikerFields)
        answer should equal (biker)
      }
    }

    "fail with empty name on change biker name" in {
      client.changeBikerName(bikerID.bikerID).invoke(BikerChangeFields(None,None,None,None,None)).map { answer =>
        fail("Emtpy name should raise an error!")
      }.recoverWith {
        case (err: BadRequest) =>
          err.exceptionMessage.detail should be("Biker name cannot be empty!")
      }
    }

    "change biker name" in {
      client.changeBikerName(bikerID.bikerID).invoke(BikerChangeFields(Some("Some biker name changed!"),None,None,None,None)).map { answer =>
        answer.bikerID should equal (bikerID.bikerID)
      }
    }

    "get biker changed name" in {
      client.getBiker(bikerID.bikerID).invoke.map { answer =>
        answer.bikerFields.name should equal ("Some biker name changed!")
      }
    }

    "change biker AvatarB64" in {
      client.changeBikerAvatarB64(bikerID.bikerID).invoke(BikerChangeFields(None,Some("AvatarB64 changed!"),None,None,None)).map { answer =>
        answer.bikerID should equal (bikerID.bikerID)
      }
    }

    "get biker changed AvatarB64" in {
      client.getBikerAvatarB64(bikerID.bikerID).invoke.map { answer =>
        answer.avatarb64 should equal (Some("AvatarB64 changed!"))
      }
    }

    "change biker blood type" in {
      client.changeBikerBloodType(bikerID.bikerID).invoke(BikerChangeFields(None,None,Some("Some blood type changed!"),None,None)).map { answer =>
        answer.bikerID should equal (bikerID.bikerID)
      }
    }

    "get biker changed blood type" in {
      client.getBiker(bikerID.bikerID).invoke.map { answer =>
        answer.bikerFields.bloodType should === (Some("Some blood type changed!"))
      }
    }

    "change biker mobile" in {
      client.changeBikerMobile(bikerID.bikerID).invoke(BikerChangeFields(None,None,None,Some("Some mobile changed!"),None)).map { answer =>
        answer.bikerID should equal (bikerID.bikerID)
      }
    }

    "get biker changed mobile" in {
      client.getBiker(bikerID.bikerID).invoke.map { answer =>
        answer.bikerFields.mobile should equal (Some("Some mobile changed!"))
      }
    }

    "change biker email" in {
      client.changeBikerEmail(bikerID.bikerID).invoke(BikerChangeFields(None,None,None,None,Some("Some email changed!"))).map { answer =>
        answer.bikerID should equal (bikerID.bikerID)
      }
    }

    "get biker changed email" in {
      client.getBiker(bikerID.bikerID).invoke.map { answer =>
        answer.bikerFields.email should equal (Some("Some email changed!"))
      }
    }

    "deactivate biker" in {
      client.deactivateBiker(bikerID.bikerID).invoke().map { answer =>
        answer.bikerID should equal (bikerID.bikerID)
      }
    }

    "find biker deactivated" in {
      client.getBikerIsActive(bikerID.bikerID).invoke.map { answer =>
        answer.active should equal (false)
      }
    }

    "activate biker" in {
      client.activateBiker(bikerID.bikerID).invoke().map { answer =>
        answer.bikerID should equal (bikerID.bikerID)
      }
    }

    "find biker activated" in {
      client.getBikerIsActive(bikerID.bikerID).invoke.map { answer =>
        answer.active should equal (true)
      }
    }

    //TODO test the biker read side response (even when empty) (the read side is updated by the test execution?)
    //"find biker in bikers sequence from the read side" in {
    //  client.getBikers(Some(1),Some(10)).invoke.map { answer =>
    //    answer.seq.filter(biker => (biker.bikerID.id==bikerID.id)).last.bikerID.id should be (bikerID.id)
    //  }
    //}
  }

  override protected def afterAll() = server.stop()
}