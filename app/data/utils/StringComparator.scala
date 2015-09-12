package data.utils

import java.util.Locale
import java.text.Normalizer

object StringComparator {

  /**
   * Compares two strings and returns the Levenshtein distance between them. This is a coefficient indicating the
   * minimum number of single-character edits (i.e. insertions, deletions or substitutions) required to change one
   * string into the other.
   * @param a first string
   * @param b second string
   * @return Levenshtein distance
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

  /**
   * Returns the percentage of difference between two strings. This is calculated as the Levenshtein distance over the
   * max length of a or b.
   * @param a any string
   * @param b any other string
   * @return difference percentage
   */
  def difference(a: String, b: String): Double = {
    val aComp: String = generateComparable(a)
    val bComp: String = generateComparable(b)
    levenshteinDistance(aComp, bComp) / Math.min(aComp.length, bComp.length) * 100
  }

  /**
   * Returns the percentage of similarity between two strings. This is calculated as the 100 minus the Levenshtein
   * distance over the max length of a or b.
   * @param a any string
   * @param b any string
   * @return similarity percentage
   */
  def similarity(a: String, b: String): Double = 100 - difference(a, b)

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
