package interchange.controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.ws._

import scala.concurrent.Future
//import scala.concurrent.ExecutionContext.Implicits.global

import play.api.Play.current

import interchange.util.InterchangeConfig
import interchange.util.CrowdAuth

import views.html.acewebui._

object AceWebUI extends Controller {
  implicit val context = play.api.libs.concurrent.Execution.Implicits.defaultContext
  case class CropCache(crid: String, name: String)
  implicit val cropCacheReads = Json.reads[CropCache]

  def index = Action.async { implicit request =>
    WS.url(InterchangeConfig.cropsitedbUrl+"/cache/crop").get().map {
      res => {
        val cropCache = (res.json).validate[Seq[CropCache]]
        cropCache match {
          case s: JsSuccess[Seq[CropCache]] => Ok(views.html.acewebui.index(s.get))
          case e: JsError => BadRequest(JsError.toFlatJson(e).toString())
        }
      }
    }
  }

  def uploadForm = CrowdAuth { implicit request =>
    val u = request.session.get("uid")
    Ok(u.getOrElse("undone"))
  }

  def parseUpload = TODO
  def finalizeUpload = TODO
}
