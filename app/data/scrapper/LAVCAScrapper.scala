package data.scrapper

import javax.inject.Inject

import org.jsoup.Jsoup
import org.jsoup.nodes.{Element, Document}
import data.utils.NameValidator
import dal.FundDAO
import scala.concurrent.ExecutionContext

class LAVCAScrapper @Inject()(fundDAO: FundDAO)(implicit ec: ExecutionContext) {

  def run() =  {
    val lavcaDoc: Document = Jsoup.connect("http://lavca.org/membership/current-members-lavca/").get()
    val auxArray: Array[Element] = Array.empty
    for (link : Element <- lavcaDoc.select("a[href]").toArray(auxArray)){
      val url: String = link.attr("abs:href")
      if(!url.contains("lavca")) {
        val name: String = NameValidator.validateName(link.text())
        fundDAO.create(name, url)
      }
    }
  }


}
