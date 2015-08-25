package data.utils

//noinspection SpellCheckingInspection
case class Url(host: String, path: String, queryString: Map[String, String]) {
  //wwww
  def thirdLevelDomain : Option[String] = {
    containsSubdomain match {
      case true => Option[String](host.substring(0, host.indexOf(".")))
      case false => Option.empty[String]
    }
  }

  //mydomainname
  def secondLevelDomain = {
    val idx: Int = host.indexOf(".")
    containsSubdomain match {
      case true => host.substring(idx, host.indexOf(".", idx + 1))
      case false => host.substring(0, idx)
    }
  }

  //com
  def topLevelDomain = {
    val idx: Int = host.indexOf(".")
    containsSubdomain match {
      case true => host.substring(idx, host.indexOf(".", idx + 1))
      case false => host.substring(idx)
    }
  }

  def sameDomain(url : Url) = host == url.host

  private def containsSubdomain = host.indexOf(".", host.indexOf(".") + 1) > -1
}

object Url {
  /**
   * Constructs an url object from and url string
   * @param url string
   * @return url object
   */
  def apply(url: String): Url = {
    if (UrlValidator.isValid(url)) {
      val validUrl: String = UrlValidator.validate(url)
      val idxOfPath: Int = validUrl.indexOf("/")
      val idxOfQuery: Int = validUrl.indexOf("?")

      val host = validUrl.substring(0, idxOfPath)

      val path = validUrl.substring(idxOfPath, idxOfQuery match {
        case -1 => validUrl.length
        case _ => idxOfQuery
      })

      val queryString: String = idxOfQuery match {
        case -1 => null
        case _ => validUrl.substring(idxOfQuery + 1)
      }


      val map: Map[String, String] = queryString match {
        case null => Map.empty[String, String]
        case _ => argumentMap(queryString)
      }

      Url(host, path, map)
    } else throw new IllegalArgumentException("Not an url")
  }

  /**
   * Constructs a map from a query string, taking the arguments as the key set and their given values as the value set
   * @param queryString query string
   * @return map mapping argument to value
   */
  def argumentMap(queryString : String): Map[String, String] = {
    var offset = 0
    var map: Map[String, String] = Map.empty
    while (queryString.indexOf("=", offset) > -1) {
      val middle: Int = queryString.indexOf("=", offset)
      val indexOfAmp: Int = queryString.indexOf("&", offset)
      val end: Int = {
        indexOfAmp match {
          case -1 => queryString.length
          case _ => indexOfAmp
        }
      }
      val argument: String = queryString.substring(offset, middle)
      val value: String = queryString.substring(middle + 1, end)
      map += (argument -> value)
      offset = end + 1
    }
    map
  }

}
