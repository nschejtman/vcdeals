package net.utils

object UrlValidator {
  def isValid(url : String) = url.matches("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")

  def validate(url : String) = {
    var aux = url
    val start: Int = aux.indexOf("://")
    aux = start match {
      case -1 => aux
      case _ => aux.substring(start + 3)
    }
    val idxOfPath: Int = aux.indexOf("/")
    val idxOfQuery: Int = aux.indexOf("?")
    (idxOfPath, idxOfQuery) match {
      case (-1, -1) => aux + "/"
      case (-1, _) => aux.substring(0, idxOfQuery) + "/" + aux.substring(idxOfQuery, aux.length)
      case (_, _) => aux
    }
  }


}
