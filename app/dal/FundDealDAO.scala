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

    override def * = (id, fundId, dealId, verified) <>((FundDealRelation.apply _).tupled, FundDealRelation.unapply)
  }

  private val fundDealRelations = TableQuery[FundDealTable]

  def create(fundId: Long, dealId: Long): Future[FundDealRelation] = db.run {
    (fundDealRelations.map(p => (p.fundId, p.dealId, p.verified))
      returning fundDealRelations.map(_.id)
      into ((vars, id) => FundDealRelation(id, vars._1, vars._2, vars._3))
      ) +=(fundId, dealId, false)
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

}
