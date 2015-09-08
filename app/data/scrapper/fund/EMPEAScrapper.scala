package data.scrapper.fund

import javax.inject.Inject

import dal.FundDAO
import data.utils.NameValidator
import org.jsoup.Jsoup
import org.jsoup.nodes.{Element, Document}

import scala.concurrent.ExecutionContext

class EMPEAScrapper @Inject()(fundDAO: FundDAO)(implicit ec: ExecutionContext) {

  //TODO refactor scrapper style
  def run() = {
    val empeaDoc: Document = Jsoup.connect("http://empea.org/members/list-of-members/").userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").get()
    val auxArray: Array[Element] = Array.empty
    for (link: Element <- empeaDoc.select("a[href]").toArray(auxArray)) {
      val url: String = link.attr("abs:href")
      if (!url.contains("lavca")) {
        val name: String = NameValidator.validateName(link.text())
        fundDAO.create(name, url)
      }
    }
  }


}
