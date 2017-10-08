package com.bikeride.utils.servicelocator.dns

import akka.actor.ActorRef
import com.lightbend.lagom.scaladsl.client.CircuitBreakerComponents

trait DNSServiceLocatorComponents extends CircuitBreakerComponents {
  def serviceLocatorService: ActorRef =
    actorSystem.actorOf(ServiceLocator.props, ServiceLocator.Name)

  lazy val serviceLocator: DNSServiceLocator =
    new DNSServiceLocator(serviceLocatorService, actorSystem, circuitBreakers, executionContext)
}
