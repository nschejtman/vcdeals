package data.scrapper

trait Scrapper[T]{
  /**
   * Given a link, obtain a list of the
   * @param link link to the source web page
   * @return sequence of obtained elements from source
   */
  def getContent(link : String) : Seq[T]
}