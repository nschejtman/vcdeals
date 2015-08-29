package data.scrapper.filter

import net.Url

object FundAsocFilter extends LinkFilter{
  override def filter(url: Url): Boolean = {
    url.host match {
      case "lavca" => false
      case "empea" => false
      case _ => true
    }
  }
}
