package controllers

import javax.inject.Inject

import com.google.inject.Singleton
import dal.{ScrapperStatisticDAO, FundDAO, DealDAO}
import data.scrapper.deal.DoubleIterationDealScrapper
import data.scrapper.filter.{FundAsocFilter, SocialFilter}
import models.{Fund, Deal}
import net.{Protocols, Url}
import net.utils.{JSoupUrlExtractor, UrlValidator}
import org.jsoup.Jsoup
import org.jsoup.nodes.{Element, Document}
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.{JsValue, Json}
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

  def getDealHub = Action { implicit request => {
    Ok(views.html.deal.hub.render(request.session))
  }

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

  def put = Action.async { implicit request =>
    dealForm.bindFromRequest().fold(
      errorForm => {
        Future.successful(Redirect(routes.Application.index()))
      },
      deal => {
        dealDao.update(deal.id, deal.name, deal.url, deal.verified)
        Future.successful(Redirect(routes.DealController.getDealHub()))
      }
    )
  }


  def updateAllFunds() = Action { implicit request => {
    val deals: Seq[Deal] = List()
    val scraper = new DoubleIterationDealScrapper(statisticDAO)
    fundDao.list().foreach(f => f.foreach(fund => try {
      scraper.getContent(fund.url).foreach(d => dealDao.create(d.name, d.url))
    } catch {
      case _: Exception =>
    }))

    Ok(views.html.deal.hub.render(request.session))
  }


  }

  def postExtractUrl = Action { implicit request =>
    val urlStr: String = Form(single("url" -> text)).bindFromRequest().get
    UrlValidator.isValid(urlStr) match {
      case true =>
        val deals: Seq[Deal] = new DoubleIterationDealScrapper(statisticDAO).getContent(urlStr)
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

  def getCrunchBase = Action { implicit request =>
    dealDao.list().foreach(d => d.seq.foreach{
      deal =>



        dealDao.updateNameById(deal.id,searchCrunchBase(deal.name))

    })
    Ok(views.html.deal.hub.render(request.session))
  }

  def searchCrunchBase(Name: String): Boolean = {
    try {
      val url = "https://api.crunchbase.com/v/3/organizations?name=" + Name + "&user_key=0916cd0764ac298d65304c70ee2b6873"

      val response = Jsoup.connect(url).ignoreContentType(true).execute().body();
      val json: JsValue = Json.parse(response)
      System.out.println(json);
      var cant = (json \ "data" \ "paging" \ "total_items").as[Int]
      System.out.println(cant);
      return cant > 0
    }
    catch {
      case _: Exception => {
        false
      }

    }
  }
}

