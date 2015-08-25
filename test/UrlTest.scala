import data.utils.Url
import org.junit.{Assert, Test}

class UrlTest {
  @Test
  def testUrlGenerationFromString() ={
    Assert.assertTrue(Url("www.google.com", "/this/is/the/path.html", Map.empty + ("query" -> "1") + ("name" -> "2"))
      .equals(Url("http://www.google.com/this/is/the/path.html?query=1&name=2")))
  }

  @Test
  def testUrlGenerationFromString2() ={
    Assert.assertTrue(Url("soundcloud.com", "/tiesto", Map.empty[String, String])
      .equals(Url("https://soundcloud.com/tiesto")))
  }

  @Test
  def testUrlGenerationFromString3() ={
    Assert.assertTrue(Url("soundcloud.com", "/", Map.empty[String, String]).equals(Url("soundcloud.com")))
  }

  @Test
  def testSameDomain() = {
    Assert.assertTrue(Url("drive.google.com").sameDomain(Url("drive.google.com/this/is/the/path")))
  }
  
  @Test
  def testSameTopLevel()= {
    Assert.assertTrue(Url("google.com").topLevelDomain == Url("twitter.com").topLevelDomain)
  }

  @Test
  def testSameSecondLevel()={
    Assert.assertTrue(Url("mail.google.com").secondLevelDomain == Url("drive.google.com").secondLevelDomain)
  }

  @Test
  def testSameThirdLevel()={
    Assert.assertTrue(Url("www.google.com").thirdLevelDomain.get == Url("www.amazon.com").thirdLevelDomain.get)
  }


}
