package controllers

import models.Fund
import play.api._
import play.api.data.Form
import play.api.mvc._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile
import tables.FundTable
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.data.Forms.mapping
import play.api.data.Forms.text
import play.api.data.Forms.longNumber


class FundController extends Controller with FundTable with HasDatabaseConfig[JdbcProfile] {
  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  import driver.api._

  //create an instance of the table
  val Funds = TableQuery[Funds]

  val fundForm = Form(
    mapping(
      "id" -> longNumber(),
      "name" -> text(),
      "url" -> text()
    )(Fund.apply)(Fund.unapply)
  )

  def list = Action.async { implicit rs =>
    db.run(Funds.result).map(res => Ok(views.html.fund.list(res.toList)))
  }

//  def view(id: Long) = Action {
//    Ok(views.html.fund.view(null))
//  }

  def getCreate = Action {
    Ok(views.html.fund.form(fundForm))
  }

  def postCreate = Action{ implicit request =>
    val form: Form[Fund] = fundForm.bindFromRequest()
    if(form.hasErrors) BadRequest(views.html.fund.form(form))
    else{
      db.run {
        Funds += form.get
      }
      Redirect(routes.FundController.list)
    }
  }

//  def getUpdate(id: Long) = Action {
//    Ok(null)
//  }
//
//  def postUpdate(id: Long) = Action {
//    Ok(null)
//  }


}
