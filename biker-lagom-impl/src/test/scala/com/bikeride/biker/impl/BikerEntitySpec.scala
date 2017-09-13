package com.bikeride.biker.impl

import java.util.UUID

import akka.Done
import akka.actor.ActorSystem
import akka.testkit.TestKit
import com.lightbend.lagom.scaladsl.testkit.{PersistentEntityTestDriver, ServiceTest}
import com.lightbend.lagom.scaladsl.playjson.JsonSerializerRegistry
import com.lightbend.lagom.scaladsl.server.LocalServiceLocator
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}

class BikerEntitySpec extends WordSpec with Matchers with BeforeAndAfterAll {

  lazy val server = ServiceTest.startServer(
    ServiceTest.defaultSetup.withCassandra(true)
  ) { ctx =>
    new BikerApplication(ctx) with LocalServiceLocator
  }

  private val system = ActorSystem("BikerEntitySpecSystem",JsonSerializerRegistry.actorSystemSetupFor(BikerSerializerRegistry))

  override protected def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  private def withTestDriver(block: PersistentEntityTestDriver[BikerCommand, BikerEvent, Option[BikerState]] => Unit): Unit = {
    val driver = new PersistentEntityTestDriver(system, new BikerEntity, "test-biker-1")
    block(driver)
    driver.getAllIssues should have size 0
  }

  "biker entity" should {
    val bikerId = UUID.randomUUID()
    val biker = BikerState(bikerId,"Some biker name",Some("AvatarB64"),Some("Some blood type"),Some("Some mobile"),Some("Some email"),true)
    "create a biker " in withTestDriver { driver =>
      val outcome = driver.run(CreateBiker(biker))
      outcome.replies should contain only Done
    }
    "update a biker name " in withTestDriver { driver =>
      val outcome = driver.run(ChangeBikerName(BikerChange(bikerId,Some("Some biker name updated"),None,None,None,None)))
      outcome.replies should contain only Done
    }
    "get a test" in withTestDriver { driver =>
      val outcome = driver.run(GetBiker)
      outcome.replies should contain only biker
    }

    /*
        "allow updating the greeting message" in withTestDriver { driver =>
          val outcome1 = driver.run(UseGreetingMessage("Hi"))
          outcome1.events should contain only GreetingMessageChanged("Hi")
          val outcome2 = driver.run(Hello("Alice"))
          outcome2.replies should contain only "Hi, Alice!"
        }
    */
  }
}
