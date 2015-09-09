package controllers

import javax.inject.Inject

import com.google.inject.Singleton
import dal.DealDAO
import models.Deal
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

import scala.concurrent.{Future, ExecutionContext}

@Singleton
class DealController @Inject()(dealDao: DealDAO)(implicit ec: ExecutionContext) extends Controller {
  val dealForm = Form(
    mapping(
      "id" -> longNumber(),
      "name" -> text(),
      "url" -> text(),
      "verified" -> boolean
    )(Deal.apply)(Deal.unapply)
  )


  def getDealHub = Action {
    Ok(views.html.deal.hub.render())
  }

  def getList = Action.async {
    dealDao.list().map(deals =>
      Ok(Json.toJson(deals))
    )
  }

  def post = Action.async { implicit request =>
    dealForm.bindFromRequest().fold(
      errorForm => {
        Future.successful(Redirect(routes.Application.index))
      },
      deal => {
        dealDao.create(deal.name, deal.url, deal.verified).map { _ =>
          Redirect(routes.DealController.getDealHub)
        }
      }
    )
  }
}

