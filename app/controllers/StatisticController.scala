package controllers

/**
 * Created by Tomas on 08/10/2015.
 */
import javax.inject.Inject

import com.google.inject.Singleton
import dal.{ScrapperStatisticDAO, FundDAO}
import data.scrapper.fund.{EMPEAScrapper, LAVCAScrapper}
import models.{ScrapperStatistic, Fund}
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class StatisticController @Inject()(statisticDAO: ScrapperStatisticDAO)(implicit ec: ExecutionContext) extends Controller {

  val statisticForm = Form(
    mapping(
      "id" -> longNumber(),
      "url" -> text(),
      "successful" -> boolean
    )(ScrapperStatistic.apply)(ScrapperStatistic.unapply)
  )

  //API actions

  def getList = Action.async {
    statisticDAO.list().map(statistics =>
      Ok(Json.toJson(statistics))
    )
  }

  def getStatisticHub = Action { implicit request => { Ok(views.html.statistics.statistics.render(request.session))}

  }



}
