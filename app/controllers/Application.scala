package controllers

import data.scrapper.deal.DoubleIterationDealScrapper
import models.Deal
import net.utils.UrlValidator
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.{Future, _}


class Application extends Controller {

  def index() = Action {
    Ok(views.html.index.render())
  }

  def demoDoubleIt = Action {
    Ok(views.html.doubleIteratorScrapperDemo.render())
  }

  def getDeals = Action { implicit request =>
    val urlStr: String = Form(single("url" -> text)).bindFromRequest().get
    UrlValidator.isValid(urlStr) match {
      case true =>
        val deals: Seq[Deal] = DoubleIterationDealScrapper.getContent(urlStr)
        Ok(Json.toJson(deals))
      case false => BadRequest
    }
  }



}
