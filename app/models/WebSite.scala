package models

import java.util.Date

case class WebSite(id : Long, host: String, lastUpdate : Date) {

  def visited = lastUpdate == null
}
