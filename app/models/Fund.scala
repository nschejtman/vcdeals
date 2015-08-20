package models

import play.api.libs.json.Json

case class Fund(id : Long, name : String, url : String, verified : Boolean)
object Fund {
  implicit val fundFormat = Json.format[Fund]

}
