package com.bikeride.utils.security

import java.security.Principal
import java.util.UUID
import javax.security.auth.Subject

import com.lightbend.lagom.scaladsl.api.security.ServicePrincipal
import com.lightbend.lagom.scaladsl.api.transport._
import com.lightbend.lagom.scaladsl.server.ServerServiceCall
import com.typesafe.config.ConfigFactory
import play.api.libs.json._
import pdi.jwt.{Jwt, JwtJson}

import scala.collection.immutable
import scala.util.{Failure, Success}

sealed trait UserPrincipal extends Principal {
  val userId: UUID
  override def getName: String = userId.toString
  override def implies(subject: Subject): Boolean = false
}

object UserPrincipal {

  case class ServicelessUserPrincipal(userId: UUID) extends UserPrincipal

  case class UserServicePrincipal(userId: UUID, servicePrincipal: ServicePrincipal) extends UserPrincipal with ServicePrincipal {
    override def serviceName: String = servicePrincipal.serviceName
  }

  def of(userId: UUID, principal: Option[Principal]) = {
    principal match {
      case Some(servicePrincipal: ServicePrincipal) =>
        UserPrincipal.UserServicePrincipal(userId, servicePrincipal)
      case other =>
        UserPrincipal.ServicelessUserPrincipal(userId)
    }
  }
}

object SecurityHeaderFilter extends HeaderFilter {

  //TODO how to implement internal calls?
  override def transformClientRequest(request: RequestHeader) = {
    //TODO check how to tell which service is going out and use the appropriate headers
    request.withHeaders(immutable.Seq(
      ("bikerideClientID",ConfigFactory.load().getString("authentication.clientID")),
      ("bikerideClientToken",createAutoLoginToken.authToken))
    )
  }

  override def transformServerRequest(request: RequestHeader) = {
    // http://pauldijou.fr/jwt-scala/samples/jwt-core/
    // https://auth0.com/blog/refresh-tokens-what-are-they-and-when-to-use-them/
    request.getHeader("bikerideClientID") match {
      case Some(clientID) => {
        request.getHeader("bikerideClientToken") match {
          case Some(token) =>
            val tokenContent: TokenContent = decodeToken(token)
            if (tokenContent.clientId == UUID.fromString(clientID) && (Jwt.isValid(token,JWTConstants.secret,Seq(JWTConstants.algorithm)))) {
                request.withPrincipal(UserPrincipal.of(tokenContent.userId, request.principal))
            }
            else request
          case None => request
        }
      }
      case None => request
    }
  }

  override def transformServerResponse(response: ResponseHeader, request: RequestHeader) = response
  override def transformClientResponse(response: ResponseHeader, request: RequestHeader) = response

  lazy val Composed = HeaderFilter.composite(SecurityHeaderFilter, UserAgentHeaderFilter)

  private def decodeToken(token: String) = {
    val jsonTokenContent = JwtJson.decode(token, JWTConstants.secret, Seq(JWTConstants.algorithm))
    jsonTokenContent match {
      case Success(json) => Json.parse(json.content).as[TokenContent]
      case Failure(_) => {
        throw Forbidden(s"Unable to decode token")
      }
    }
  }

  def createAutoLoginToken(): Token = {
    val tokenContent = TokenContent(
      UUID.fromString(ConfigFactory.load().getString("authentication.clientID")),
      UUID.fromString(ConfigFactory.load().getString("authentication.bikerID")),
      ConfigFactory.load().getString("authentication.bikerName"),
      false)
    JwtTokenUtil.generateAuthTokenOnly(tokenContent)
  }
}

object ServerSecurity {

  def authenticated[Request, Response](serviceCall: UUID => ServerServiceCall[Request, Response]) =
    ServerServiceCall.compose { requestHeader =>
      requestHeader.principal match {
        case Some(userPrincipal: UserPrincipal) =>
          serviceCall(userPrincipal.userId)
        case other =>
          throw Forbidden("User not authenticated")
      }
    }
}

object ClientSecurity {
  def authenticate(userId: UUID): RequestHeader => RequestHeader = { request =>
    request.withPrincipal(UserPrincipal.of(userId, request.principal))
  }
}

