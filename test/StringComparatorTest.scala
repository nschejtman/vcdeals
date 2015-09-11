import data.utils.StringComparator
import org.junit.{Assert, Test}

class StringComparatorTest {

  @Test
  //noinspection SpellCheckingInspection
  def testLevenshteinDistance() = {
    Assert.assertTrue(StringComparator.compare("cat", "bat") == 1)
    Assert.assertTrue(StringComparator.compare("kitten", "sitting") == 3)
    Assert.assertTrue(StringComparator.compare("book", "back") == 2)
    Assert.assertTrue(StringComparator.compare("donate", "different") == 7)
    Assert.assertTrue(StringComparator.compare("DONATE", "donate") == 0)
    Assert.assertTrue(StringComparator.compare("(d@o!n$a%t&e*)5", "donate") == 0)
    Assert.assertTrue(StringComparator.compare("áéíóú", "aeiou") == 0)
    Assert.assertTrue(StringComparator.compare("áéíóú", "âêîôû") == 0)
    Assert.assertTrue(StringComparator.compare("áéíóú", "äëïöü") == 0)
  }


}
