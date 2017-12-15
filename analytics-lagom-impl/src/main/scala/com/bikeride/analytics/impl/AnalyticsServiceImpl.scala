package com.bikeride.analytics.impl

import akka.Done
import akka.stream.Materializer
import akka.stream.scaladsl.Flow
import com.bikeride.analytics.api.{AnalyticsAccrual, AnalyticsService}
import com.bikeride.authentication.api.{AuthenticationService, BikerLoggedIn}
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.{PersistentEntityRegistry, ReadSide}
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraSession

import scala.concurrent.{ExecutionContext, Future}

class AnalyticsServiceImpl (authnService: AuthenticationService,
                            session: CassandraSession,
                            persistentEntityRegistry: PersistentEntityRegistry,
                            readside: ReadSide)
                            (implicit ec: ExecutionContext, mat: Materializer) extends AnalyticsService{
  //private def refFor(id: String) = persistentEntityRegistry.refFor[AnalyticsEntity](id)


  authnService
    .authenticationTopic()
    .subscribe
    .atLeastOnce(
      //Flow[BikerLoggedIn].map{ msg =>
      Flow[Any].map{ msg =>
        print("############################# getEvents():"+msg)
        Done
      }
    )

  override def getStatistics() = ServiceCall { _ =>
    Future(AnalyticsAccrual("",""))
  }

}