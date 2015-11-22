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

  def create(name: String, url: String): Future[Deal] ={
    if (find(name) != null) {db.run {

      (deals.map(p => (p.name, p.url, p.verified))
        returning deals.map(_.id)
        into ((vars, id) => Deal(id, vars._1, vars._2, vars._3))
        ) +=(name, url, false)
    }} else return null

  }

  /*def update(deal: Deal): Unit ={
    val existent= deals.filter(_.id === deal.id)
    System.out.println(existent);
    if (existent != null){
      existent.update(deal).run()

    }
  }*/

  def updateNameById(Id: Long, verified : Boolean) = {
    if(verified){
      db.run{
        val q = for {l <- deals if l.id === Id} yield l.verified

        q.update(verified)


      }
    }

  }

  def update(id: Long, name: String, url: String, verified: Boolean) = db.run {
    val q = for {l <- deals if l.id === id} yield (l.name, l.url, l.verified)
    q.update(name, url, verified)
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

  def find(id: Long) : Future[Deal] = db.run {
    deals.filter(_.id === id).result.head
  }

  def find(Name : String) : Future[Deal] = db.run {
    deals.filter(_.name === Name).result.head
  }


}
