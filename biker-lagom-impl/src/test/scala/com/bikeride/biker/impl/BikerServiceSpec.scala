package com.bikeride.biker.impl

import java.util.UUID

import com.lightbend.lagom.scaladsl.server.LocalServiceLocator
import com.lightbend.lagom.scaladsl.testkit.ServiceTest
import org.scalatest.{AsyncWordSpec, BeforeAndAfterAll, Matchers}
import com.bikeride.biker.api._
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
  private var bikerID: BikerID = BikerID(UUID.randomUUID())
  private var biker = Biker(bikerID,bikerFields)

  "biker-lagom service" should {
    "create biker" in {
      client.createBiker.invoke(bikerFields).map { answer =>
        bikerID = answer
        answer shouldBe a [BikerID]
      }
    }

    "get biker by ID" in {
      client.getBiker(bikerID.id).invoke.map { answer =>
        biker = Biker(bikerID,bikerFields)
        answer should equal (biker)
      }
    }

    "fail with empty name on change biker name" in {
      client.changeBikerName(bikerID.id).invoke(BikerChangeFields(None,None,None,None,None)).map { answer =>
        fail("Emtpy name should raise an error!")
      }.recoverWith {
        case (err: BadRequest) =>
          err.exceptionMessage.detail should be("Biker name cannot be empty!")
      }
    }

    "change biker name" in {
      client.changeBikerName(bikerID.id).invoke(BikerChangeFields(Some("Some biker name changed!"),None,None,None,None)).map { answer =>
        answer.id should equal (bikerID.id)
      }
    }

    "get biker changed name" in {
      client.getBiker(bikerID.id).invoke.map { answer =>
        answer.bikerFields.name should equal ("Some biker name changed!")
      }
    }

    "change biker AvatarB64" in {
      client.changeBikerAvatarB64(bikerID.id).invoke(BikerChangeFields(None,Some("AvatarB64 changed!"),None,None,None)).map { answer =>
        answer.id should equal (bikerID.id)
      }
    }

    "get biker changed AvatarB64" in {
      client.getBikerAvatarB64(bikerID.id).invoke.map { answer =>
        answer.avatarb64 should equal (Some("AvatarB64 changed!"))
      }
    }

    "change biker blood type" in {
      client.changeBikerBloodType(bikerID.id).invoke(BikerChangeFields(None,None,Some("Some blood type changed!"),None,None)).map { answer =>
        answer.id should equal (bikerID.id)
      }
    }

    "get biker changed blood type" in {
      client.getBiker(bikerID.id).invoke.map { answer =>
        answer.bikerFields.bloodType should === (Some("Some blood type changed!"))
      }
    }

    "change biker mobile" in {
      client.changeBikerMobile(bikerID.id).invoke(BikerChangeFields(None,None,None,Some("Some mobile changed!"),None)).map { answer =>
        answer.id should equal (bikerID.id)
      }
    }

    "get biker changed mobile" in {
      client.getBiker(bikerID.id).invoke.map { answer =>
        answer.bikerFields.mobile should equal (Some("Some mobile changed!"))
      }
    }

    "change biker email" in {
      client.changeBikerEmail(bikerID.id).invoke(BikerChangeFields(None,None,None,None,Some("Some email changed!"))).map { answer =>
        answer.id should equal (bikerID.id)
      }
    }

    "get biker changed email" in {
      client.getBiker(bikerID.id).invoke.map { answer =>
        answer.bikerFields.email should equal (Some("Some email changed!"))
      }
    }

    "deactivate biker" in {
      client.deactivateBiker(bikerID.id).invoke().map { answer =>
        answer.id should equal (bikerID.id)
      }
    }

    "find biker deactivated" in {
      client.getBikerIsActive(bikerID.id).invoke.map { answer =>
        answer.active should equal (false)
      }
    }

    "activate biker" in {
      client.activateBiker(bikerID.id).invoke().map { answer =>
        answer.id should equal (bikerID.id)
      }
    }

    "find biker activated" in {
      client.getBikerIsActive(bikerID.id).invoke.map { answer =>
        answer.active should equal (true)
      }
    }

    //TODO properly assert the biker read side response (even when empty)
    //"find biker in bikers sequence from the read side" in {
    //  client.getBikers(Some(1),Some(10)).invoke.map { answer =>
    //    answer.seq.filter(biker => (biker.bikerID.id==bikerID.id)).last.bikerID.id should === (bikerID.id)
    //  }
    //}
  }

  override protected def afterAll() = server.stop()
}