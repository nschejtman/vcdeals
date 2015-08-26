package net

object Protocols extends Enumeration {

  protected case class Val(name: String) {
    def buildLinkString(url: Url) = name + "://" + url.toString
    //TODO create infix method for adding a protocol to an url
  }

  type Protocols = Val
  val HTTP = Val("http")
  val HTTPS = Val("https")
}