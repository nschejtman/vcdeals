package data.scrapper.filter

import net.Url

trait LinkFilter {
  def filter(url : Url) : Boolean
  def filter(urls : Seq[Url]) : Seq[Url] = urls.filter(filter)


}
