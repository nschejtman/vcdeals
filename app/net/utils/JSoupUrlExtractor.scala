package net.utils

import javax.inject.Inject

import dal.ScrapperStatisticDAO
import data.scrapper.Scrapper
import models.{Deal, ScrapperStatistic}
import net.{Protocols, Url}
import org.jsoup.Jsoup
import org.jsoup.nodes.{Element, Document}

import scala.concurrent.ExecutionContext


case class JSoupUrlExtractor@Inject()(statisticDAO: ScrapperStatisticDAO)(implicit ec: ExecutionContext) {

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
      statisticDAO.create(url.toString,successful = true)
      elements.map(_.attr("href")).filter(UrlValidator.isValid).map(e => buildUrl(e, url))

    }
    catch {
      case _: Exception => {
        statisticDAO.create(url.toString,successful = false)
        Seq.empty}
    }
  }

}
