package com.bikeride.utils.security

import java.util.UUID
import pdi.jwt.{JwtAlgorithm, JwtClaim, JwtJson}
import play.api.libs.json.{Format, Json}

object JwtTokenUtil {
  val secret = "2o9WVew2qIFKQqV]2g:TKnzOpIGXQ@pr-!osvOi=v|,CpVe/]7m:@nNWM|LN.GAlxeL.eIn*%oS$m=*A,3N@hyGwZ=yA&=%>]g!Zt@=jMj|)KL8[qzylS7z0hjgRhA~m:&wR=LBT-;8V"
  val authExpiration = 300
  val refreshExpiration = 86400
  val algorithm = JwtAlgorithm.HS512

  def generateTokens(content: TokenContent)(implicit format: Format[TokenContent]): Token = {
    val authClaim = JwtClaim(Json.toJson(content).toString())
      .expiresIn(authExpiration)
      .issuedNow

    val refreshClaim = JwtClaim(Json.toJson(content.copy(isRefreshToken = true)).toString())
      .expiresIn(refreshExpiration)
      .issuedNow

    val authToken = JwtJson.encode(authClaim, secret, algorithm)
    val refreshToken = JwtJson.encode(refreshClaim, secret, algorithm)

    Token(
      authToken = authToken,
      refreshToken = Some(refreshToken)
    )
  }

  def generateAuthTokenOnly(content: TokenContent)(implicit format: Format[TokenContent]): Token = {
    val authClaim = JwtClaim(Json.toJson(content.copy(isRefreshToken = false)).toString())
      .expiresIn(authExpiration)
      .issuedNow

    val authToken = JwtJson.encode(authClaim, secret, algorithm)

    Token(
      authToken = authToken,
      None
    )
  }
}

case class Token(authToken: String, refreshToken: Option[String])
object Token {
  implicit val format: Format[Token] = Json.format
}


case class TokenContent(clientId: UUID, userId: UUID, username: String, isRefreshToken: Boolean = false)
object TokenContent {
  implicit val format: Format[TokenContent] = Json.format
}