package com.bikeride.biker.impl

import java.util.UUID
import akka.actor.ActorSystem
import akka.testkit.TestKit
import com.lightbend.lagom.scaladsl.testkit.PersistentEntityTestDriver
import com.lightbend.lagom.scaladsl.playjson.JsonSerializerRegistry
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}

class BikerEntitySpec extends WordSpec with Matchers with BeforeAndAfterAll {
  private val system = ActorSystem("BikerEntitySpecSystem",
    JsonSerializerRegistry.actorSystemSetupFor(BikerSerializerRegistry))

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
    "create a biker " in withTestDriver { driver =>
      val outcome = driver.run(CreateBiker(bikerId,"Some biker name","A+"))
      outcome.replies should contain only bikerId
    }
    "update a biker " in withTestDriver { driver =>
      val outcome = driver.run(UpdateBiker(bikerId,"Some biker name updated","A+"))
      outcome.replies should contain only bikerId
    }
    "get a test" in withTestDriver { driver =>
      val outcome = driver.run(GetBiker)
      outcome.replies should contain only BikerState(bikerId,"Some biker name updated","A+")
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
