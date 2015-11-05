package data.scrapper.filter

import net.Url

object FundAsocFilter extends LinkFilter{
  override def filter(url: Url): Boolean = {
    !(url.host.contains("lavca") || url.host.contains("empea"))
  }
}
