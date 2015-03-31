package interchange.util

import play.api.mvc.ActionBuilder
import play.api.mvc.Cookie
import play.api.mvc.Request
import play.api.mvc.Result
import play.api.mvc.Results.Redirect
import play.api.libs.ws._

import scala.concurrent.Future

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.Play.current
import play.Logger

import interchange.util._

object CrowdAuth extends ActionBuilder[Request] {
  def invokeBlock[A](req: Request[A], block: (Request[A]) => Future[Result]) = {
    val token = req.cookies.get("crowd.token_key")
    def redirectLogin = Future.successful(Redirect(InterchangeConfig.tollboothUrl+"?for="+InterchangeConfig.myScheme+"://"+req.host+req.uri))

    token match {
      case Some(t) => {
        WS.url(InterchangeConfig.tollboothUrl+"/email/"+t.value).get.flatMap { r =>
          if(r.status == 200) {
            val j = (r.json \ "email").as[String]
            val res = block(req)
            req.session.get("uid") match {
              case Some(u) => {
                if (j == u) res else redirectLogin 
              }
              case None => res.map(_.withSession("uid"->j))
            }
          } else {
            redirectLogin
          }
        }
      }
      case None => redirectLogin 
    }
  }
}

object ProfileAction extends ActionBuilder[Request] {
  def invokeBlock[A](req: Request[A], block: (Request[A]) => Future[Result]) = {
    val token = req.cookies.get("crowd.token_key")
    val uid   = req.session.get("uid")
    uid match {
      case Some(u) => Logger.debug("Found uid"); block(req)
      case None    => {
        token match {
          case None => Logger.debug("No auth token found"); block(req)
          case Some(t) => {
            // The user is logged in somewhere but not displaying at this point
            WS.url(InterchangeConfig.tollboothUrl+"/email/"+t.value).get.flatMap { r =>
              if(r.status == 200) {
                val j = (r.json \ "email").as[String]
                block(req).map(_.withSession("uid"->j))
              } else {
                block(req)
              }
            }
          }
        }
      }
    }
  }
}
