package data.utils

//noinspection SpellCheckingInspection
case class Url(host: String, path: String, queryString: String) {
  //wwww
  def thirdLevelDomain = host.substring(0, host.indexOf("."))

  //mydomainname
  def secondLevelDomain = {
    val start: Int = host.indexOf(".")
    host.substring(start, host.indexOf(".", start + 1))
  }

  //com
  def topLevelDomain = {
    val start: Int = host.indexOf(".", host.indexOf(".") + 1)
    host.substring(start)
  }
}

object Url {
  def apply(url: String): Url = {
    if (UrlValidator.isValid(url)) {
      val host = url.substring(0, url.indexOf("/"))
      val path = url.substring(url.indexOf("/"))
      Url(host, path, null)
    } else throw new IllegalArgumentException("Not an url")
  }
}
