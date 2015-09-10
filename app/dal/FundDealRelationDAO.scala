package dal

import javax.inject.Inject

import com.google.inject.Singleton
import models.{Deal, FundDealRelation, Fund}
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import scala.concurrent.{Future, ExecutionContext}

@Singleton
class FundDealRelationDAO @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  private class FundDealRelationTable(tag: Tag) extends Table[FundDealRelation](tag, "FUND_DEAL_RELATION") {
    def fundId = column[Long]("FUND_ID")

    def dealId = column[Long]("DEAL_ID")

    def verified = column[Boolean]("VERIFIED")

    override def * = (fundId, dealId, verified) <>((FundDealRelation.apply _).tupled, FundDealRelation.unapply)
  }

  private val fundDealRelations = TableQuery[FundDealRelationTable]

  def create(fundId : Long, dealId : Long): Future[Unit] = db.run {
    fundDealRelations += FundDealRelation(fundId, dealId, verified = false)
  }.map(_ => ())

  def listByFund(fundId : Long) : Future[Seq[FundDealRelation]] = db.run {
    fundDealRelations.filter(_.fundId === fundId).result
  }

  def listByDeal(dealId : Long) : Future[Seq[FundDealRelation]] = db.run {
    fundDealRelations.filter(_.dealId === dealId).result
  }

  def list() : Future[Seq[FundDealRelation]] = db.run{
    fundDealRelations.result
  }







}
