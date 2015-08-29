package data.scrapper.filter

import net.Url

object PortfolioFilter extends LinkFilter{
  override def filter(url: Url): Boolean = {
    val Pattern = "*portfolio*".r
    val Pattern2 = "*deals*".r
    url.path match {
      case Pattern(c) => true
      case _ => false
    }
  }
}
