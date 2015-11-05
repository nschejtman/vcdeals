package data.scrapper.fund

import javax.inject.Inject

import dal.FundDAO
import data.scrapper.filter.{FundAsocFilter, UrlReductionFilter, SocialFilter}
import data.utils.NameValidator
import net.Url
import net.utils.UrlValidator
import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}

import scala.concurrent.ExecutionContext

class LAVCAScrapper @Inject()(fundDAO: FundDAO)(implicit ec: ExecutionContext) {

  //noinspection SpellCheckingInspection
  def run() = {
    val lavcaDoc: Document = Jsoup.connect("http://lavca.org/membership/current-members-lavca/").get()
    val tupledLinks: Array[(String, Url)] = lavcaDoc.select("a[href]").toArray(Array.empty[Element]).filter(e => UrlValidator.isValid(e.attr("abs:href"))).map(e => (e.text(), Url(e.attr("abs:href"))))
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
