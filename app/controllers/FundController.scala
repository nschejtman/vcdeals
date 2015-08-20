package controllers

import javax.inject.Inject

import dal.FundDAO
import data.scrapper.{ScrapperOne, LAVCAScrapper}
import models.Fund
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

import scala.concurrent.{ExecutionContext, Future}


class FundController @Inject()(fundDao: FundDAO)(implicit ec: ExecutionContext) extends Controller {

  val fundForm = Form(
    mapping(
      "id" -> longNumber(),
      "name" -> text(),
      "url" -> text(),
      "boolean" -> boolean
    )(Fund.apply)(Fund.unapply)
  )

  def list = Action.async {
    fundDao.list().map(funds =>
      Ok(Json.toJson(funds))
    )
  }

  def verificationStats = Action.async {
    fundDao.list().map(funds =>
      Ok(getVerifiedStatsJson(funds))
    )
  }

  private def getVerifiedStatsJson(funds: Seq[Fund]) = {
    var verified = 0
    var unverified = 0
    funds.foreach(fund =>
      fund.verified match {
        case true => verified += 1
        case false => unverified += 1
      }
    )

//    val jsonStr =
      "{[{label : \"verified\", value : %1$s}, {label : \"verified\", value : %2$s}]}" format(verified, unverified)
//    Json.parse(jsonStr)
  }

  def getList = Action {
    Ok(views.html.fund.list())
  }

  def getCreate = Action {
    Ok(views.html.fund.form(fundForm))
  }

  def postCreate = Action.async { implicit request =>
    fundForm.bindFromRequest().fold(
      errorForm => {
        Future.successful(Redirect(routes.Application.index))
      },
      fund => {
        fundDao.create(fund.name, fund.url).map { _ =>
          Redirect(routes.FundController.list)
        }
      }
    )
  }

  def demoDeals() = Action {
    ScrapperOne.run1()
    ScrapperOne.run2()
    Redirect(routes.Application.index)
  }

  def updateFunds(): Unit = {
    val LAVCAScrapper: LAVCAScrapper = new LAVCAScrapper(fundDao)
    LAVCAScrapper.run()
  }

}

