package net

import net.utils.UrlValidator

//noinspection SpellCheckingInspection
case class Url(host: String, path: String, query: Map[String, String]) {
  //wwww
  def thirdLevelDomain : Option[String] = {
    containsSubdomain match {
      case true => Option[String](host.substring(0, host.indexOf(".")))
      case false => Option.empty[String]
    }
  }

  //mydomainname
  def secondLevelDomain = {
    try {
      val idx: Int = host.indexOf(".")
      containsSubdomain match {
        case true => host.substring(idx + 1, host.indexOf(".", idx + 1))
        case false => host.substring(0, idx)
      }
    } catch {case _ : Exception => ""}
  }

  //com
  def topLevelDomain = {
    val idx: Int = host.indexOf(".")
    containsSubdomain match {
      case true => host.substring(host.indexOf(".", idx + 1))
      case false => host.substring(idx)
    }
  }

  def sameDomain(url : Url) = try{secondLevelDomain == url.secondLevelDomain && topLevelDomain == url.topLevelDomain} catch {case _ : Exception => false}

  override def toString = {
    var aux = host ++ path
    if (query.nonEmpty){
      aux += "?"
      query.foreach(t =>
        aux += t._1 + "=" + t._2 + "&"
      )
    }
    aux.substring(0, aux.length - 1)
  }

  private def containsSubdomain = {
    val fIdx: Int = host.indexOf(".")
    host.indexOf(".", fIdx + 1) > -1 && !TopLevelDomains.contains(host.substring(fIdx, host.indexOf(".", fIdx + 1)))
  }
}

object Url {
  /**
   * Constructs an url object from and url string
   * @param url string
   * @return url object
   */
  def apply(url: String): Url = {

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


