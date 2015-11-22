package net.utils

object UrlValidator {
  def isValid(url : String): Boolean = {
    val isvalid =url.matches("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")

    return isvalid
  }
  //url.matches("/^(https?:\\/\\/)?([\\da-z\\.-]+)\\.([a-z\\.]{2,6})([\\/\\w \\.-]*)*\\/?$/")
  //url.matches("/((([A-Za-z]{3,9}:(?:\\/\\/)?)(?:[\\-;:&=\\+\\$,\\w]+@)?[A-Za-z0-9\\.\\-]+|(?:www\\.|[\\-;:&=\\+\\$,\\w]+@)[A-Za-z0-9\\.\\-]+)((?:\\/[\\+~%\\/\\.\\w\\-_]*)?\\??(?:[\\-\\+=&;%@\\.\\w_]*)#?(?:[\\.\\!\\/\\\\\\w]*))?)/")
  //url.matches("^(((ht|f)tps?\\:\\/\\/)|~/|/)?([a-zA-Z]{1}([\\w\\-]+\\.)+([\\w]{2,5})(:[\\d]{1,5})?)/?(\\w+\\.[\\w]{3,4})?((\\?\\w+=\\w+)?(&\\w+=\\w+)*)?")
  //
  //url.matches("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")



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
