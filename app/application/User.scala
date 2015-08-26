package application

import application.UserRoles.UserRoles

case class User(username : String, password : String, roles : List[UserRoles]) {
  def authenticate : Boolean = username.equals("admin") && password.equals("password")
}

object User{

}