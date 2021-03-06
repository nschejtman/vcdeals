package dal

import javax.inject.{Inject, Singleton}
import models.Fund
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import scala.concurrent.{Future, ExecutionContext}
import scala.util.{Failure, Success}

@Singleton
class FundDAO @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  private class FundTable(tag: Tag) extends Table[Fund](tag, "FUND") {
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)

    def name = column[String]("NAME")

    def url = column[String]("URL")

    def verified = column[Boolean]("VERIFIED")

    override def * = (id, name, url, verified) <>((Fund.apply _).tupled, Fund.unapply)
  }

  private val funds = TableQuery[FundTable]

  def create(name: String, url: String): Future[Fund] = db.run {
    (funds.map(p => (p.name, p.url, p.verified))
      returning funds.map(_.id)
      into ((vars, id) => Fund(id, vars._1, vars._2, vars._3))
      ) +=(name, url, false)
  }

  def create(name: String, url: String, verified: Boolean): Future[Fund] = db.run {
    (funds.map(p => (p.name, p.url, p.verified))
      returning funds.map(_.id)
      into ((vars, id) => Fund(id, vars._1, vars._2, vars._3))
      ) +=(name, url, verified)
  }

  def list(): Future[Seq[Fund]] = db.run {
    funds.result
  }

  def update(id: Long, name: String, url: String, verified: Boolean) = db.run {
    val q = for {l <- funds if l.id === id} yield (l.name, l.url, l.verified)
    q.update(name, url, verified)
  }

  def find(id: Long): Future[Fund] = db.run {
    funds.filter(_.id === id).result.head
  }

  def find(Name: String): Future[Fund] = db.run {
    funds.filter(_.name === Name).result.head
  }

}
