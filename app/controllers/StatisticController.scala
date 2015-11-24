package controllers

/**
 * Created by Tomas
 * Date: 08/10/2015
 * Project: vcdeals
 */
import javax.inject.Inject

import com.google.inject.Singleton
import dal.{FundDAO, ScrapperStatisticDAO}
import models.{Fund, ScrapperStatistic}
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

import scala.concurrent.{Future, ExecutionContext}

@Singleton
class StatisticController @Inject()(statisticDAO: ScrapperStatisticDAO, fundDAO: FundDAO)
                                   (implicit ec: ExecutionContext) extends Controller {

  val statisticForm = Form(
    mapping(
      "id" -> longNumber(),
      "url" -> text(),
      "successful" -> boolean
    )(ScrapperStatistic.apply)(ScrapperStatistic.unapply)
  )

  //API actions

  def getList = Action.async {
    val futureStatistics: Future[Seq[ScrapperStatistic]] = statisticDAO.list()
    val futureFunds: Future[Seq[Fund]] = fundDAO.list()

    val values = for {
      statistics <- futureStatistics
      funds <- futureFunds
    } yield (statistics, funds)

    values.map { case (statistics, funds) =>
      val result = findStaticsForFunds(statistics, funds)
      Ok(Json.toJson(result))
    }
  }

  private def findStaticsForFunds(statistics: Seq[ScrapperStatistic], funds: Seq[Fund]) = funds
    .filter(fund => statistics.exists(_.url.startsWith(fund.url)))
    .map(findStaticsForFund(statistics))

  private def findStaticsForFund(statistics: Seq[ScrapperStatistic])(fund: Fund) = {
      val successful = statistics.filter(_.url.startsWith(fund.url)).forall(_.successful)
      ScrapperStatistic(0, fund.url, successful)
  }

  def getStatisticHub = Action { implicit request => { Ok(views.html.statistics.statistics.render(request.session))}

  }

  def getCountSuccess = Action.async{
    statisticDAO.list().map( statistics => Ok(Json.toJson(statistics.count(s => s.successful))))
  }

  def getCountAll = Action.async{
    statisticDAO.list().map( statistics => Ok(Json.toJson(statistics.size)))
  }
}
