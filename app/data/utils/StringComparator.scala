package data.utils

import java.util.Locale

object StringComparator {

  /**
   * Compares two strings and returns the similarity percentage. It's calculated as a percentage from the total length
   * of string b. In other words it's the percentage of how much a fits in b.
   * Research more at:
   * http://stackoverflow.com/questions/15303631/what-are-some-algorithms-for-comparing-how-similar-two-strings-are#
   * @param a first string
   * @param b second string
   * @return similarity percentage
   */
  def compare(a : String, b: String): Double ={
    val aComp = generateComparable(a)
    val bComp = generateComparable(b)
    val aLength: Int = aComp.length
    val bLength: Int = bComp.length
    if (aLength == 0 || bLength == 0) return 0
    var bi : Int = b.indexOf(a.charAt(0))
    if (bi == -1) return 0
    var ai=0
    var acc = 0
    while(ai < aLength && bi < bLength){
      if (aComp.charAt(ai) == bComp.charAt(bi)){
        acc+=1
        ai+=1
        bi+=1
      } else
        while (bComp.charAt(bi) != aComp.charAt(ai)) bi+=1
    }
    acc / bLength * 100
  }

  /**
   * Generates a standard string without spaces, special characters or numbers, only lower case letters.
   * @param a regular string
   * @return standard string
   */
  private def generateComparable(a :String): String = {
    a.replaceAll("[^a-zA-Z]", "").toLowerCase(Locale.US)
  }

}
