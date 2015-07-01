package controllers

import play.api._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import play.api.mvc._
import slick.driver.JdbcProfile
import tables.FundTable

class Application extends Controller with FundTable with HasDatabaseConfig[JdbcProfile]{
  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  import driver.api._

  //create an instance of the table
  val Funds = TableQuery[Funds]

  def index() = Action {
    Ok(views.html.index.render())
  }


}
