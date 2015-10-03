package controllers


import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Action, Controller, Security}
import views.html

/**
 * Created by Tomas on 27/08/2015.
 */
class Auth extends Controller {

  val loginForm = Form(
    tuple(
      "email" -> text,
      "password" -> text
    ) verifying ("Invalid email or password", result => result match {
      case (email, password) => check(email, password)
    })
  )

  def check(username: String, password: String) = {
    (username == "admin" && password == "1234")
  }

  def login = Action { implicit request =>
    Ok(html.navigation.login(loginForm))
  }

  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.navigation.login(formWithErrors)),
      user => Redirect("").withSession(Security.username -> user._1)
    )
  }

  def logout = Action {
    Redirect("").withNewSession.flashing(
      "success" -> "You are now logged out."
    )
  }
}