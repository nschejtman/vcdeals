package data.scrapper.filter

import net.Url

object UrlReductionFilter extends LinkFilter{
  /**
   * Returns true if passes filter or false otherwise.
   * @param url to test against filter
   * @return true if passes filter or false if should be filtered.
   */
  override def filter(url: Url): Boolean =  !(url.host.contains("fb") || url.host.contains("bit") || url.host.contains("bitly") || url.host.contains("linkd"))


}
