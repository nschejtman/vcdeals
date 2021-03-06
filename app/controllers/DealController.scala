package controllers

import javax.inject.Inject

import com.google.inject.Singleton
import dal.{FundDealDAO, ScrapperStatisticDAO, FundDAO, DealDAO}
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
class DealController @Inject()(dealDao: DealDAO,fundDao : FundDAO,
                               fundDealDAO: FundDealDAO, statisticDAO: ScrapperStatisticDAO)(implicit ec: ExecutionContext) extends Controller {
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
    fundDao.list().foreach(funds => funds.foreach(fund =>
      try {
        val scraperDeals: Seq[Deal] = scraper.getContent(fund.url)
        scraperDeals.foreach( d => createDeal(fund,d,searchCrunchBase(d.name)))
        //scraperDeals.foreach(d => createDeal(fund, dealDao.create(d.name, d.url)))
        /*        scraperDeals.foreach(d => dealDao.create(d.name, d.url))
        scraperDeals.foreach(d => fundDealDAO.create(fund.id, d.id))*/
    } catch {
      case _: Exception =>
    }))

    Ok(views.html.deal.hub.render(request.session))
  }}

  def createDeal(f: Fund, d: Deal,details : String) = {
    dealDao.create(d.name, d.url).foreach( deal =>
      fundDealDAO.create(f.id, deal.id,details)
    )
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



        dealDao.updateNameById(deal.id,verifyCrunchBase(deal.name))

    })
    Ok(views.html.deal.hub.render(request.session))
  }

  def verifyCrunchBase(Name: String): Boolean = {
    try {
      val url = "https://api.crunchbase.com/v/3/organizations?name=" + Name + "&user_key=0916cd0764ac298d65304c70ee2b6873"

      val response = Jsoup.connect(url).ignoreContentType(true).execute().body();
      val json: JsValue = Json.parse(response)

      val cant = (json \ "data" \ "paging" \ "total_items").as[Int]

      return cant > 0
    }
    catch {
      case _: Exception => {
        false
      }

    }
  }

  def searchCrunchBase(Name : String):String = {
    try {
      var details=""
      var url = "https://api.crunchbase.com/v/3/organizations?name=" + Name + "&user_key=0916cd0764ac298d65304c70ee2b6873"

      var response = Jsoup.connect(url).ignoreContentType(true).execute().body();
      var json: JsValue = Json.parse(response)

      var cant = (json \ "data" \ "paging" \ "total_items").as[Int]
      if (cant == 1){
        var permaLink = ((json \ "data" \ "items")(0) \ "properties" \ "permalink").as[String];
       url = "https://api.crunchbase.com/v/3/organizations/" + permaLink + "?user_key=0916cd0764ac298d65304c70ee2b6873"

       response = Jsoup.connect(url).ignoreContentType(true).execute().body()
        json = Json.parse(response)

        (json \\ "founders").filter(f => (f \ "paging" \ "total_items").as[Int] > 0).foreach(j => (j \\ "items").foreach(i => details+=("Fundador= " + i )))



        (json \\ "investors").filter(f => (f \ "paging" \ "total_items").as[Int] > 0).foreach(j => (j \\ "items").foreach(i => details+=("Inversor= " + i )))


        (json \\ "funding_rounds").filter(f => (f \ "paging" \ "total_items").as[Int] > 0).foreach(j => (j \\ "items").foreach(i => details+=("Ronda de Inversion= " + i )))

        (json \\ "investments").filter(f => (f \ "paging" \ "total_items").as[Int] > 0).foreach(j => (j \\ "items").foreach(i => details+=("Inversiones= " + i )))

        (json \\ "ipo").filter(f => (f \ "paging" \ "total_items").as[Int] > 0).foreach(j => (j \\ "items").foreach(i => details+=("IPO= " + i )))

      details = details.replace("\"","\\\"")
        details = details.replace("'","\'")
      }

      return details
    }
    catch {
      case _: Exception => {
        ""
      }

    }
  }
}

