package models

import play.api.libs.json.Json

case class Deal(id : Long, name : String, url: String, verified : Boolean) {


}

object Deal {
  implicit val dealFormat = Json.format[Deal]
}
