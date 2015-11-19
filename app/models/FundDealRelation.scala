package models

import play.api.libs.json.Json

case class FundDealRelation(id: Long, fundId : Long, dealId : Long, verified : Boolean){

}

object FundDealRelation {
  implicit val fundDealRelationFormat = Json.format[FundDealRelation]
}
