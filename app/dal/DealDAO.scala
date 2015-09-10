package dal

import javax.inject.{Inject, Singleton}

import models.Deal
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DealDAO @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  private class DealTable(tag: Tag) extends Table[Deal](tag, "DEAL") {
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)

    def name = column[String]("NAME")

    def url = column[String]("URL")

    def verified = column[Boolean]("VERIFIED")

    override def * = (id, name, url, verified) <>((Deal.apply _).tupled, Deal.unapply)
  }

  private val deals = TableQuery[DealTable]

  def create(name: String, url: String): Future[Deal] = db.run {
    (deals.map(p => (p.name, p.url, p.verified))
      returning deals.map(_.id)
      into ((vars, id) => Deal(id, vars._1, vars._2, vars._3))
      ) +=(name, url, false)
  }

  def create(name: String, url: String, verified : Boolean): Future[Deal] = db.run {
    (deals.map(p => (p.name, p.url, p.verified))
      returning deals.map(_.id)
      into ((vars, id) => Deal(id, vars._1, vars._2, vars._3))
      ) +=(name, url, verified)
  }

  def list(): Future[Seq[Deal]] = db.run {
    deals.result
  }

}
