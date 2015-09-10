package data.scrapper.filter

import net.Url

object FundFilter extends LinkFilter{
  override def filter(url: Url): Boolean = {
    url.host.indexOf("cap") > -1
  }
}
