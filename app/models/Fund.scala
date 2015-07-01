package models

import play.api.libs.json.Json

case class Fund(id : Long, name : String, url : String)

object Fund {
  implicit val catFormat = Json.format[Fund]
}