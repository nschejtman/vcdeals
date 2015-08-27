package models

import play.api.libs.json.Json

case class Deal(name : String, url: String) {


}

object Deal {
  implicit val dealFormat = Json.format[Deal]
}
