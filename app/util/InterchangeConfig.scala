package interchange.util;

import play.api.Play

object InterchangeConfig {
  val myScheme = Play.current.configuration.getString("interchange.scheme").getOrElse("http")
  val tollboothUrl = rewriteUrl(Play.current.configuration.getString("tollbooth.baseurl").getOrElse("http://localhost:9110"))
  val cropsitedbUrl = rewriteUrl(Play.current.configuration.getString("cropsitedb.baseurl").getOrElse("http://localhost:8080/cropsitedb/2"))

  def rewriteUrl(url: String):String = {
    if (url.endsWith("/")) {
      url.substring(0, url.length-1)
    } else {
      url
    }
  }
  
}
