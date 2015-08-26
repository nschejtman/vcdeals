package net

object TopLevelDomains {
   //TODO : extract list from https://en.wikipedia.org/wiki/List_of_Internet_top-level_domains
   val domains : List[String] = ".com" :: ".gov" :: ".org" :: ".net" :: ".edu" :: Nil
   def contains(domain : String) = domains.contains(domain)
 }
