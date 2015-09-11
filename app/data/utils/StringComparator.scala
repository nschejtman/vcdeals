package data.utils

import java.util.Locale
import java.text.Normalizer

object StringComparator {

  /**
   * Compares two strings and returns the similarity percentage
   * @param a first string
   * @param b second string
   * @return similarity percentage
   */
  def compare(a: String, b: String): Int = levenshteinDistance(generateComparable(a), generateComparable(b))

  /**
   * Generates a standard string without spaces, special characters or numbers, only lower case letters.
   * @param a regular string
   * @return standard string
   */
  private def generateComparable(a: String): String = {
    Normalizer.normalize(a, Normalizer.Form.NFD).replaceAll("[^a-zA-Z]", "").toLowerCase(Locale.US)
  }

  //noinspection SpellCheckingInspection
  private def levenshteinDistance(a: String, b: String) = {
    val aLength: Int = a.length
    val bLength: Int = b.length
    val distance: Array[Array[Int]] = Array.ofDim[Int](aLength + 1, bLength + 1)
    for (i <- 0 to aLength) distance(i)(0) = i
    for (j <- 0 to bLength) distance(0)(j) = j
    def min(a: Int, b: Int, c: Int) = Math.min(Math.min(a, b), c)
    for (i <- 1 to aLength)
      for (j <- 1 to bLength) {
        val d0: Int = distance(i - 1)(j) + 1
        val d1: Int = distance(i)(j - 1) + 1
        val d2: Int = distance(i - 1)(j - 1) + {
          if (a.charAt(i - 1) == b.charAt(j - 1)) 0; else 1
        }
        distance(i)(j) = min(d0, d1, d2)
      }
    distance(aLength)(bLength)
  }


}
