package net.utils

import net.{Protocols, Url}
import org.jsoup.Jsoup
import org.jsoup.nodes.{Element, Document}

object JSoupUrlExtractor {
  //TODO make async
  //TODO add some filtering to filter by html where <a href="href">html</a>
  def extractUrls(url: Url): Seq[Url] = {
    try {
      val document: Document = Jsoup.connect(Protocols.HTTP.buildLinkString(url)).get()
      val elements = document.select("a[href]").toArray(Array.empty[Element])
      def buildUrl(url: String, baseUrl: Url) = {
        try {
        url.charAt(0) match {
          case '/' => Url(baseUrl.host + url)
          case _ => Url(url)
        }
        }catch { case _ : Exception => Url("")}
      }
      elements.map(_.attr("href")).filter(UrlValidator.isValid).map(e => buildUrl(e, url))
    }
    catch {
      case _: Exception => Seq.empty
    }
  }

}
