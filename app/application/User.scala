package application

import play.api.libs.json.Json

case class User(id: Long, username : String, password : String/*, roles : List[UserRoles]*/) {
  def authenticate : Boolean = username.equals("admin") && password.equals("password")
}

object User{

    implicit val userFormat = Json.format[User]

}