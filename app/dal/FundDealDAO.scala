package dal

import javax.inject.{Inject, Singleton}
import models.{FundDealRelation, Fund}
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import scala.concurrent.{Future, ExecutionContext}

@Singleton
class FundDealDAO @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  private class FundDealTable(tag: Tag) extends Table[FundDealRelation](tag, "FUND_DEAL") {
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)

    def fundId = column[Long]("FUND_ID")

    def dealId = column[Long]("DEAL_ID")

    def verified = column[Boolean]("VERIFIED")

    def details = column[String]("DETAILS")

    override def * = (id, fundId, dealId, verified,details) <>((FundDealRelation.apply _).tupled, FundDealRelation.unapply)
  }

  private val fundDealRelations = TableQuery[FundDealTable]

  def create(fundId: Long, dealId: Long,details : String): Future[FundDealRelation] = {

  var existentRelation : FundDealRelation = null
    findByFundDeal(fundId,dealId).onComplete(f => existentRelation = f.getOrElse(null))
    if (existentRelation == null){


    db.run {

    (fundDealRelations.map(p => (p.fundId, p.dealId, p.verified,p. details))
      returning fundDealRelations.map(_.id)
      into ((vars, id) => FundDealRelation(id, vars._1, vars._2, vars._3,vars._4))
      ) +=(fundId, dealId, false,details)
  }}else{
      update(existentRelation.id,existentRelation.verified,details)
      Future{existentRelation}
    }
  }

  /*
    def create(fundId: String, dealId: String, verified : Boolean): Future[FundDealRelation] = db.run {
      (fundDealRelations.map(p => (p.fundId, p.dealId, p.verified))
        returning fundDealRelations.map(_.id)
        into ((vars, id) => FundDealRelation(id, vars._1, vars._2, vars._3))
        ) +=(fundId, dealId, verified)
    }*/

  def list(): Future[Seq[FundDealRelation]] = db.run {
    fundDealRelations.result
  }

  def find(id: Long) : Future[FundDealRelation] = db.run {
    fundDealRelations.filter(_.id === id).result.head
  }

  def findByFund(fundId : Long ) : Future[FundDealRelation] = db.run {
    fundDealRelations.filter(_.fundId === fundId).result.head
  }

  def findByFundDeal(fundId : Long,dealId : Long): Future[FundDealRelation] = db.run {
    fundDealRelations.filter(_.fundId === fundId).filter(_.dealId === dealId).result.head
  }

  def update(id: Long, verified: Boolean,details : String) = db.run {
    val q = for {l <- fundDealRelations if l.id === id} yield (l.details, l.verified)
    q.update(details, verified)
  }

}
