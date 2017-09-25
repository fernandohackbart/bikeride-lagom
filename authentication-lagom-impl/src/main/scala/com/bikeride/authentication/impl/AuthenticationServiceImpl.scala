package com.bikeride.authentication.impl

import java.util.UUID
import java.time.Instant

import akka.stream.Materializer
import com.bikeride.authentication.api
import com.bikeride.authentication.api.AuthenticationService
import com.bikeride.biker.api.{BikerID, BikerService, BikerToken}
import com.bikeride.utils.security._
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.transport.{BadRequest, NotFound}
import com.lightbend.lagom.scaladsl.persistence.{PersistentEntityRegistry, ReadSide}
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraSession

import scala.concurrent.ExecutionContext
import scala.util.Random

class AuthenticationServiceImpl (bikerService: BikerService,
                        persistentEntityRegistry: PersistentEntityRegistry,
                        session: CassandraSession,
                        readside: ReadSide)
                       (implicit ec: ExecutionContext, mat: Materializer) extends AuthenticationService{

  private def refFor(id: String) = persistentEntityRegistry.refFor[AuthenticationEntity](id)

  override def generatePIN = ServiceCall { req =>

    var bikerID: UUID = UUID.randomUUID()
    var bikerName: String = "ERROR"
    //TODO: check if the email or mobile are provided

    //TODO: get this from the materialized view
    session.select("SELECT id,name FROM biker_contacts WHERE email=? OR mobile=?",req.email,req.mobile).map { row =>
      bikerID = row.getUUID("id")
      bikerName =  row.getString("name")
    }

    val pinID: String = Random.alphanumeric.take(6).mkString
    val expirationTime: Instant = Instant.now.plusSeconds(300)
    refFor(pinID).ask(GeneratePIN(AuthenticationPINState(pinID,req.clientID.clientId,bikerID,bikerName,expirationTime))).map { _ =>
      //TODO: send the PIN by email or SMS
      //api.GeneratePINResponse("PIN generated and sent!")

      //TODO: removed this, it is only for testing
      api.GeneratePINResponse(pinID.toString)
    }
  }

  override def validatePIN = ServiceCall { req =>
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

  override def createBiker = ServiceCall { req =>
    //TODO: check if the token is needed
    bikerService.createBiker.invoke(req)
  }
}