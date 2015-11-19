package controllers

import javax.inject.Inject

import com.google.inject.Singleton
import dal.FundDAO
import data.scrapper.fund.{EMPEAScrapper, LAVCAScrapper}
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
      "verified" -> boolean
    )(Fund.apply)(Fund.unapply)
  )

  //API actions

  def getList = Action.async {
    fundDao.list().map(funds =>
      Ok(Json.toJson(funds))
    )
  }

  def post = Action.async { implicit request =>
    fundForm.bindFromRequest().fold(
      errorForm => {
        Future.successful(Redirect(routes.Application.index()))
      },
      fund => {
        fundDao.create(fund.name, fund.url, fund.verified).map { _ =>
          Redirect(routes.FundController.getFundHub())
        }
      }
    )
  }

  def put = Action.async { implicit request =>
    fundForm.bindFromRequest().fold(
        errorForm => {
          errorForm.errors.foreach(p => System.out.println(p.toString))
          Future.successful(Redirect(routes.Application.index()))
      },
      fund => {
        System.out.println(fund.toString)
        fundDao.update(fund.id, fund.name, fund.url, fund.verified)
        Future.successful(Redirect(routes.FundController.getFundHub()))
      }
    )
  }

  //noinspection SpellCheckingInspection
  def updateLavca() = Action.async {
    val scrapper: LAVCAScrapper = new LAVCAScrapper(fundDao)
    scrapper.run()
    fundDao.list().map(funds =>
      Ok(Json.toJson(funds))
    )
  }

  //noinspection SpellCheckingInspection
  def updateEmpea() = Action.async {
    val scrapper: EMPEAScrapper = new EMPEAScrapper(fundDao)
    scrapper.run()
    fundDao.list().map(funds =>
      Ok(Json.toJson(funds))
    )
  }

  //Render actions

  def getFundHub = Action { implicit request => { Ok(views.html.fund.hub(fundForm))}

  }

}

