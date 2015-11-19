package controllers

import javax.inject.Inject

import com.google.inject.Singleton
import dal.{DealDAO, FundDealDAO, FundDAO}
import data.scrapper.fund.{EMPEAScrapper, LAVCAScrapper}
import models.{FundDealRelation, Fund}
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FundDealController @Inject()(fundDealDAO: FundDealDAO, fundDAO: FundDAO, dealDAO: DealDAO)(implicit ec: ExecutionContext) extends Controller {

  val fundDealForm = Form(
    mapping(
      "id" -> longNumber(),
      "fundId" -> longNumber(),
      "dealId" -> longNumber(),
      "verified" -> boolean
    )(FundDealRelation.apply)(FundDealRelation.unapply)
  )

  //API actions

  case class NiceFundDeal(fund: String, deal: String, verified: Boolean)

  implicit val niceFundDealFormat = Json.format[NiceFundDeal]


  def getList = Action.async {
    fundDealDAO.list().flatMap(fundDeals =>
      Future.sequence(fundDeals.map { fundDeal =>
        val futureFund = fundDAO.find(fundDeal.fundId)
        val futureDeal = dealDAO.find(fundDeal.dealId)

        for {
          fund <- futureFund
          deal <- futureDeal
        } yield NiceFundDeal(fund.name, deal.name, fundDeal.verified)
      })
    ).map {fundDeals =>
      Ok(Json.toJson(fundDeals))
    }
  }

  def post = Action.async { implicit request =>
    fundDealForm.bindFromRequest().fold(
      errorForm => {
        Future.successful(Redirect(routes.Application.index()))
      },
      fundDealRelation => {
        fundDealDAO.create(fundDealRelation.fundId, fundDealRelation.dealId).map { _ =>
          Redirect(routes.FundDealController.getFundDealHub())
        }
      }
    )
  }
/*
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
  }*/

  //Render actions

  def getFundDealHub = Action { implicit request => {Ok(views.html.fundDeal.hub.render(request.session))}

  }

}

