package data.scrapper.fund

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class EMPEAScrapper {
  def asd = {
    val empeaDoc: Document = Jsoup.connect("http://empea.org/members/list-of-members/").userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").get()

  }

}
