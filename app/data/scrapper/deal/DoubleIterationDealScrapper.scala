package data.scrapper.deal

import javax.inject.Inject

import dal.{ScrapperStatisticDAO, FundDAO}
import data.scrapper.Scrapper
import data.scrapper.filter.{FundAsocFilter, PortfolioFilter, SocialFilter}
import data.utils.NameValidator
import models.Deal
import net.utils.{JSoupUrlExtractor, UrlValidator}
import net.{Protocols, Url}
import org.jsoup.Jsoup
import org.jsoup.nodes.{Element, Document}

import scala.collection.mutable
import scala.concurrent.ExecutionContext

case class DoubleIterationDealScrapper@Inject()(statisticDAO: ScrapperStatisticDAO)(implicit ec: ExecutionContext) extends Scrapper[Deal] {


  override def getContent(link: String): Seq[Deal] = {
      val baseUrl: Url = Url(link)
      var links = new JSoupUrlExtractor(statisticDAO).extractUrls(baseUrl)
      val selfLinks: Seq[Url] = links.filter(l => l.sameDomain(baseUrl))
      selfLinks.foreach(l => links = links ++ new JSoupUrlExtractor(statisticDAO).extractUrls(l))
      val externalLinks: Seq[Url] = links.filter(l => !l.sameDomain(baseUrl)).filter(SocialFilter.filter).filter(FundAsocFilter.filter)
      val deals: Seq[Deal] = externalLinks.map(e => Deal(0, e.secondLevelDomain, e.host, verified = false)).distinct
      deals



  }



}
