package controllers

import javax.inject.Inject

import com.google.inject.Singleton
import dal.{ScrapperStatisticDAO, FundDAO, DealDAO}
import data.scrapper.deal.DoubleIterationDealScrapper
import models.{Fund, Deal}
import net.utils.UrlValidator
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

import scala.concurrent.{Future, ExecutionContext}

@Singleton
class DealController @Inject()(dealDao: DealDAO,fundDao : FundDAO,statisticDAO: ScrapperStatisticDAO)(implicit ec: ExecutionContext) extends Controller {
  val dealForm = Form(
    mapping(
      "id" -> longNumber(),
      "name" -> text(),
      "url" -> text(),
      "verified" -> boolean
    )(Deal.apply)(Deal.unapply)
  )

  case class Deals(deals: List[Deal])

  val dealsForm = Form[Deals](
    mapping(
      "deals" -> list(
        mapping(
          "id" -> longNumber(),
          "name" -> text(),
          "url" -> text(),
          "verified" -> boolean
        )(Deal.apply)(Deal.unapply)
      )
    )(Deals.apply)(Deals.unapply)
  )

  def getDealHub = Action { implicit request => {Ok(views.html.deal.hub.render(request.session))}

  }

  def getList = Action.async {
    dealDao.list().map(deals =>
      Ok(Json.toJson(deals))
    )
  }

  def post = Action.async { implicit request =>
    dealForm.bindFromRequest().fold(
      errorForm => {
        Future.successful(Redirect(routes.Application.index()))
      },
      deal => {
        dealDao.create(deal.name, deal.url, deal.verified).map { _ =>
          Redirect(routes.DealController.getDealHub())
        }
      }
    )
  }

  def updateAllFunds()=Action { implicit request => { val deals: Seq[Deal] = List()
    val scraper = new DoubleIterationDealScrapper(statisticDAO)
    fundDao.list().foreach(f =>  f.foreach(fund => try{scraper.getContent(fund.url).foreach(d => dealDao.create(d.name,d.url))}catch {case _: Exception =>}))

    Ok(views.html.deal.hub.render(request.session))}



  }

  def postExtractUrl = Action { implicit request =>
    val urlStr: String = Form(single("url" -> text)).bindFromRequest().get
    UrlValidator.isValid(urlStr) match {
      case true =>
        val deals: Seq[Deal] =  new DoubleIterationDealScrapper(statisticDAO).getContent(urlStr)
        Ok(Json.toJson(deals))
      case false => BadRequest
    }
  }

  def postList = Action { implicit request =>
    dealsForm.bindFromRequest().fold(
      errorForm => {
        Redirect(routes.Application.index())
      },
      data => {
        data.deals.foreach(deal => dealDao.create(deal.name, deal.url, deal.verified))
        Ok(views.html.deal.hub.render(request.session))
      }
    )
  }
}

