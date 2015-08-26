package application

object UserRoles extends Enumeration {
  type UserRoles = Value
  val admin, validator, default = Value
}
