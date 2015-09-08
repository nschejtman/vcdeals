package data.scrapper.fund

import javax.inject.Inject

import dal.FundDAO
import data.scrapper.filter.{FundAsocFilter, SocialFilter, UrlReductionFilter}
import data.utils.NameValidator
import net.Url
import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}

import scala.concurrent.ExecutionContext

class EMPEAScrapper @Inject()(fundDAO: FundDAO)(implicit ec: ExecutionContext) {

  //noinspection SpellCheckingInspection
  def run() = {
    val empeaDoc: Document = Jsoup.connect("http://empea.org/members/list-of-members/").userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").get()
    val tupledLinks: Array[(String, Url)] = empeaDoc.select("a[href]").toArray(Array.empty[Element]).map(e => (e.text(), Url(e.attr("abs:href"))))
    val filteredLinks: Array[(String, Url)] = tupledLinks.filter(t => SocialFilter.filter(t._2)).filter(t => UrlReductionFilter.filter(t._2)).filter(t => FundAsocFilter.filter(t._2))
    filteredLinks.foreach(t => {
      val name = t._1 match {
        case "" => t._2.host
        case " " => t._2.host
        case _ => NameValidator.validateName(t._1)
      }
      fundDAO.create(name, t._2.toString)
    }
    )
  }
}
