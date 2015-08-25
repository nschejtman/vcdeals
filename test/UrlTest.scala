import data.utils.Url
import org.junit.{Assert, Test}

class UrlTest {
  @Test
  def testUrlGenerationFromString() ={
    val testUrl = "http://www.google.com/this/is/the/path.html?query=1&name=2"
    val map: Map[String, String] = Map.empty + ("query" -> "1") + ("name" -> "2")
    val expected: Url = Url("www.google.com", "/this/is/the/path.html", map)
    val actual: Url = Url(testUrl)
    Assert.assertTrue(expected.equals(actual))
  }

  @Test
  def testUrlGenerationFromString2() ={
    val testUrl = "https://soundcloud.com/tiesto"
    val expected: Url = Url("soundcloud.com", "/tiesto", Map.empty[String, String])
    val actual: Url = Url(testUrl)
    Assert.assertTrue(expected.equals(actual))
  }

  @Test
  def testUrlGenerationFromString3() ={
    val testUrl = "soundcloud.com"
    val expected: Url = Url("soundcloud.com", "/", Map.empty[String, String])
    val actual: Url = Url(testUrl)
    Assert.assertTrue(expected.equals(actual))
  }

}
