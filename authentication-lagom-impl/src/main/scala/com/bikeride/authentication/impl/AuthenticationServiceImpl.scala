package com.bikeride.authentication.impl

import java.util.UUID
import java.time.Instant

import akka.stream.Materializer
import com.bikeride.authentication.api
import com.bikeride.authentication.api.AuthenticationService
import com.bikeride.biker.api._
import com.bikeride.utils.security._
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.transport.{BadRequest, NotFound, RequestHeader}
import com.lightbend.lagom.scaladsl.persistence.{PersistentEntityRegistry, ReadSide}
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraSession
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._

import scala.concurrent.{Await, ExecutionContext}
import scala.util.Random

class AuthenticationServiceImpl (bikerService: BikerService,
                        persistentEntityRegistry: PersistentEntityRegistry,
                        session: CassandraSession,
                        readside: ReadSide)
                       (implicit ec: ExecutionContext, mat: Materializer) extends AuthenticationService{

  private def refFor(id: String) = persistentEntityRegistry.refFor[AuthenticationEntity](id)

  override def generatePIN = ServiceCall { req =>

    var signBikerID: UUID = UUID.randomUUID()
    var signBikerName: String = "ERROR"

    //TODO: check if the request contains the expected JSON structure
    //TODO: check if the email or mobile are provided
    //TODO: Check if it is possible to set the hostname to the remote service (biker.api.bikeride.com)

    Await.result(bikerService.getBikerByEmail.invoke(BikerByEmailRequest(req.email.get)).map {
      case (bikerByEmailResponse) => {
        if (!bikerByEmailResponse.bikerID.isEmpty) signBikerID = bikerByEmailResponse.bikerID.get.bikerID
        if (!bikerByEmailResponse.bikerFields.isEmpty) signBikerName = bikerByEmailResponse.bikerFields.get.name
      }
    },5 seconds)

    if (signBikerName=="ERROR") throw NotFound(s"Biker with email  ${req.email.get}")
    val pinID: String = Random.alphanumeric.take(6).mkString
    val expirationTime: Instant = Instant.now.plusSeconds(300)
    refFor(pinID).ask(GeneratePIN(AuthenticationPINState(pinID,req.client.clientID,signBikerID,signBikerName,expirationTime))).map { _ =>
      //TODO: send the PIN by email or SMS
      //api.GeneratePINResponse("PIN generated and sent!")
      //TODO: removed this, it is only for testing
      api.GeneratePINResponse(pinID.toString)
    }
  }

  override def validatePIN = ServiceCall { req =>
    if (!req.pin.isEmpty)
    {
      refFor(req.pin).ask(GetPIN).map {
        case Some(authnPIN) =>
          //TODO: check the clientID provided matches the client ID in the PIN
          //req.clientID==authnPIN.clientID
          //TODO: check if the token is not expired
          val tokenContent = TokenContent(authnPIN.clientID,authnPIN.bikerID,authnPIN.bikerName,false)
          val token = JwtTokenUtil.generateAuthTokenOnly(tokenContent)
          BikerToken(BikerID(authnPIN.bikerID),token)
        case None =>
          throw NotFound(s"PIN with id $req.pin not found!")
      }
    }
    else throw NotFound(s"PIN with id $req.pin not found!")
  }

  override def createBiker = ServiceCall { req =>
    bikerService.createBiker.invoke(req)
  }

}