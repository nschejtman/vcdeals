package data.scrapper.filter

import net.Url

object UrlReductionFilter extends LinkFilter{
  /**
   * Returns true if passes filter or false otherwise.
   * @param url to test against filter
   * @return true if passes filter or false if should be filtered.
   */
  override def filter(url: Url): Boolean = url.host match {
    case "fb" => false
    case "bit" => false
    case "bitly" => false
    case "linkd" => false
    case _ => true
  }
}
