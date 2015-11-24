package dal

import javax.inject.{Inject, Singleton}
import models.ScrapperStatistic
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import scala.concurrent.{Future, ExecutionContext}
/**
 * Created by Tomas
 * Date: 08/10/2015
 * Project: vcdeals
 */
@Singleton
class ScrapperStatisticDAO @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  private class StatisticTable(tag: Tag) extends Table[ScrapperStatistic](tag, "STATISTIC") {
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)

    def url = column[String]("URL")

    def successful = column[Boolean]("SUCCESSFUL")

    override def * = (id, url, successful) <>((ScrapperStatistic.apply _).tupled, ScrapperStatistic.unapply)
  }

  private val statistics = TableQuery[StatisticTable]

  def list(): Future[Seq[ScrapperStatistic]] = db.run {
    statistics.result
  }

  def find(id: Long) : Future[ScrapperStatistic] = db.run {
    statistics.filter(_.id === id).result.head
  }

  def create(url: String, successful : Boolean): Future[ScrapperStatistic] = db.run {
    (statistics.map(s => (s.url,s.successful))
      returning statistics.map(_.id)
      into ((vars, id) => ScrapperStatistic(id, vars._1, vars._2))
      ) +=(url,successful)
  }


}
