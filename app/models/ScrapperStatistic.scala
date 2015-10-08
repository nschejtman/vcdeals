package models

import play.api.libs.json.Json

/**
 * Created by Tomas on 08/10/2015.
 */
case class ScrapperStatistic(id : Long, url : String,successful : Boolean) {

}

object ScrapperStatistic {
  implicit val statisticFormat = Json.format[ScrapperStatistic]
}