package data.scrapper.deal

import java.io.{BufferedWriter, File, FileWriter}

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

object ScrapperOne {
  def run1() = {
    val url = "http://emxcapital.com/"
    val fileName = "emxcapital.txt"
    val doc: Document = Jsoup.connect(url).get()
    val links: Elements = doc.select("a[href]")
    val file = new File(fileName)
    val bw = new BufferedWriter(new FileWriter(file))
    var investmentLinks: Elements = null
    for (i <- 0 to links.size() - 1) {
      val link = links.get(i)
      if (link.text() == "Investments") {
        val investmentUrl = link.attr("abs:href")
        val investmentDoc: Document = Jsoup.connect(investmentUrl).get()
        investmentLinks = investmentDoc.select("a[href]")
      }
    }
    if (investmentLinks != null)
      links.addAll(investmentLinks)


    bw.write("\nLinks: " + links.size())
    for (i <- 0 to links.size() - 1) {
      val link = links.get(i)
      bw.write("\n" + i + "   " + link.text() + " ")
      bw.write(link.attr("abs:href"))
    }

    bw.close()
  }

  def run2() = {
    val url = "http://mcapitalp.com/"
    val fileName = "mcapitalp.txt"
    val doc: Document = Jsoup.connect(url).timeout(0).get()
    val links: Elements = doc.select("a[href]")
    val file = new File(fileName)
    val bw = new BufferedWriter(new FileWriter(file))
    var investmentLinks: Elements = null
    for (i <- 0 to links.size() - 1) {
      val link = links.get(i)

      if (link.text() == "Investments" || link.text() == "Portfolio companies") {
        val investmentUrl = link.attr("abs:href")
        val investmentDoc: Document = Jsoup.connect(investmentUrl).timeout(0).get()
        investmentLinks = investmentDoc.select("a[href]")

      }
    }
    if (investmentLinks != null)
      links.addAll(investmentLinks)
    bw.write("\nLinks: " + links.size())
    for (i <- 0 to links.size() - 1) {
      val link = links.get(i)

      bw.write("\n" + i + "   " + link.text())
      bw.write(link.attr("abs:href"))
    }
    bw.close()
  }
}
