package controllers

import javax.inject.Inject

import dal.ScrapperStatisticDAO
import data.scrapper.deal.DoubleIterationDealScrapper
import models.Deal
import net.utils.UrlValidator
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc.{Session, Request, Action, Controller}


import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.{Future, _}


class Application @Inject()(statisticDAO: ScrapperStatisticDAO)(implicit ec: ExecutionContext) extends Controller {

  def index() = Action { implicit request => {
    Ok(views.html.index.render(request.session))
  }

  }

  def demoDoubleIt = Action { implicit request => {
    Ok(views.html.doubleIteratorScrapperDemo.render(request.session))
  }

  }

  def getDeals = Action { implicit request =>
    val urlStr: String = Form(single("url" -> text)).bindFromRequest().get
    UrlValidator.isValid(urlStr) match {
      case true =>
        val deals: Seq[Deal] = new DoubleIterationDealScrapper(statisticDAO).getContent(urlStr)
        Ok(Json.toJson(deals))
      case false => BadRequest
    }
  }



}
