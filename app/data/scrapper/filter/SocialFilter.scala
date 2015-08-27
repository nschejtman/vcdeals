package data.scrapper.filter

import net.Url

object SocialFilter extends LinkFilter {
  /**
   * Returns false if filter applies, if should be filtered
   * @param url url to test on
   * @return false if applies, false otherwise
   */
  override def filter(url: Url): Boolean = {
    url.secondLevelDomain match {
      case "facebook" => false
      case "twitter" => false
      case "google" => false
      case "linkedin" => false
      case "youtube" => false
      case "vimeo" => false
      case "tumblr" => false
      case "flickr" => false
      case "deviantart" => false
      case "dailymotion" => false
      case "foursquare" => false
      case "pinterest" => false
      case "hi5" => false
      case "instagram" => false
      case "yahoo" => false
      case "soundcloud" => false
      case _ => true
    }
  }
}
