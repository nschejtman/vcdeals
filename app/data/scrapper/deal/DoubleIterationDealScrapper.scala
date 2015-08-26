package data.scrapper.deal

import data.scrapper.Scrapper
import data.utils.NameValidator
import models.Deal
import net.utils.{JSoupUrlExtractor, UrlValidator}
import net.{Protocols, Url}
import org.jsoup.Jsoup
import org.jsoup.nodes.{Element, Document}

import scala.collection.mutable

object DoubleIterationDealScrapper extends Scrapper[Deal] {

  override def getContent(link: String): Seq[Deal] = {
    val baseUrl: Url = Url(link)
    var links = JSoupUrlExtractor.extractUrls(baseUrl)
    val selfLinks: Seq[Url] = links.filter(l => l.sameDomain(baseUrl))
    selfLinks.foreach(l => links = links ++ JSoupUrlExtractor.extractUrls(l))
    val externalLinks: Seq[Url] = links.filter(l => !l.sameDomain(baseUrl))
    val deals: Seq[Deal] = externalLinks.map(e => Deal(e.secondLevelDomain, e.host)).distinct
    deals
  }



}
