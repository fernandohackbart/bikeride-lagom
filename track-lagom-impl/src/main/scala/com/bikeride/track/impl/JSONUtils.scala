package com.bikeride.track.impl

import play.api.data.validation.ValidationError
import play.api.libs.json._

object JsonFormats {
  def singletonReads[O](singleton: O): Reads[O] = {
    (__ \ "value").read[String].collect(
      ValidationError(s"Expected a JSON object with a single field with key 'value' and value '${singleton.getClass.getSimpleName}'")
    ) {
      case s if s == singleton.getClass.getSimpleName => singleton
    }
  }
  def singletonWrites[O]: Writes[O] = Writes { singleton =>
    Json.obj("value" -> singleton.getClass.getSimpleName)
  }

  def singletonFormat[O](singleton: O): Format[O] = {
    Format(singletonReads(singleton), singletonWrites)
  }
}