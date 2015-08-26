package net.utils

object UrlValidator {
  def isValid(url : String) = true

  def validate(url : String) = {
    var aux = url
    val start: Int = aux.indexOf("://")
    if ((start > -1 && aux.indexOf("/", start + 1) < 0) || aux.indexOf("/") < 0) aux += "/"
    start match {
      case -1 => aux
      case _ => aux.substring(start + 3)
    }
  }


}
