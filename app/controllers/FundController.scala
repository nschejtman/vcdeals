package controllers

import javax.inject.Inject

import com.google.inject.Singleton
import dal.FundDAO
import data.scrapper.deal.ScrapperOne
import data.scrapper.fund.LAVCAScrapper
import models.Fund
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FundController @Inject()(fundDao: FundDAO)(implicit ec: ExecutionContext) extends Controller {

  val fundForm = Form(
    mapping(
      "id" -> longNumber(),
      "name" -> text(),
      "url" -> text(),
      "boolean" -> boolean
    )(Fund.apply)(Fund.unapply)
  )

  def getList = Action.async {
    fundDao.list().map(funds =>
      Ok(Json.toJson(funds))
    )
  }

  def getFundNew = Action {
    Ok(views.html.fund.form.render(fundForm))
  }

  def post = Action.async { implicit request =>
    fundForm.bindFromRequest().fold(
      errorForm => {
        Future.successful(Redirect(routes.Application.index))
      },
      fund => {
        fundDao.create(fund.name, fund.url).map { _ =>
          Redirect(routes.FundController.getFundList)
        }
      }
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


  def getFundList = Action {
    Ok(views.html.fund.list(fundForm))
  }

}

