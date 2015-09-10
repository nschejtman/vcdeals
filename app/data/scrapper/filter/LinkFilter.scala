package data.scrapper.filter

import net.Url

trait LinkFilter {
  /**
   * Returns true if passes filter or false otherwise.
   * @param url to test against filter
   * @return true if passes filter or false if should be filtered.
   */
  def filter(url : Url) : Boolean

  /**
   * Filters urls that do not pass the filter and returns a sequence of those that do.
   * @param urls to filter
   * @return sequence of those url that pass the filter
   */
  def filter(urls : Seq[Url]) : Seq[Url] = urls.filter(filter)


}
