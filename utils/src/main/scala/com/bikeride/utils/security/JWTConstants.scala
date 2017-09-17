package com.bikeride.utils.security

import pdi.jwt.JwtAlgorithm

object JWTConstants {
  val secret = "2o9WVew2qIFKQqV]2g:TKnzOpIGXQ@pr-!osvOi=v|,CpVe/]7m:@nNWM|LN.GAlxeL.eIn*%oS$m=*A,3N@hyGwZ=yA&=%>]g!Zt@=jMj|)KL8[qzylS7z0hjgRhA~m:&wR=LBT-;8V"
  val authExpiration = 300
  val refreshExpiration = 86400
  val algorithm = JwtAlgorithm.HS512
}
