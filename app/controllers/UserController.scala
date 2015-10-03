package controllers
import javax.inject.Inject

import com.google.inject.Singleton
import dal.UserDAO
import scala.concurrent.{ExecutionContext, Future}
import application.User
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}


/**
 * Created by florenciavelarde on 29/09/15.
 */

@Singleton
class UserController @Inject()(userDao: UserDAO)(implicit ec: ExecutionContext) extends Controller {
  val userForm = Form(
    mapping(
      "id" -> longNumber(),
      "username" -> text(),
      "password" -> text()
    )(User.apply)(User.unapply)
  )

  case class Users(users: List[User])

  val usersForm = Form[Users](
    mapping(
      "users" -> list(
        mapping(
          "id" -> longNumber(),
          "username" -> text(),
          "password" -> text()
        )(User.apply)(User.unapply)
      )
    )(Users.apply)(Users.unapply)
  )


  def getUserHub = Action {
    Ok(views.html.user.hub.render())
  }

  def getList = Action.async {
    userDao.list().map(users =>
      Ok(Json.toJson(users))
    )
  }

  def post = Action.async { implicit request =>
    userForm.bindFromRequest().fold(
      errorForm => {
        Future.successful(Redirect(routes.Application.index()))
      },
      user => {
        userDao.create(user.username, user.password).map { _ =>
          Redirect(routes.UserController.getUserHub())
        }
      }
    )
  }



  def postList = Action { implicit request =>
    usersForm.bindFromRequest().fold(
      errorForm => {
        Redirect(routes.Application.index())
      },
      data => {
        data.users.foreach(user => userDao.create(user.username, user.password))
        Ok(views.html.user.hub.render())
      }
    )
  }
}
