package interchange.controllers

import play.api._
import play.api.data._
import play.api.data.Forms._
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
  case class CreateParams(dsid: String, title: Option[String], dest: String)
  implicit val createParamsReads = Json.reads[CreateParams]


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

  def uploadFirst = CrowdAuth { implicit request =>
    Ok(views.html.acewebui.uploadfirst())
  }

  def uploadFiles = CrowdAuth.async { implicit request =>
    val friendlyForm = Form(single("friendlyName"->nonEmptyText))
    val u = request.session.get("uid")
    friendlyForm.bindFromRequest.fold(
      badData =>
        Future.successful(BadRequest("Missing Friendly Name")),
      d => {
        u match {
          case Some(x) => {
            val dsOpts = Json.obj(
              "email" -> u,
              "title" -> d,
              "freeze" -> true
              )
            WS.url(InterchangeConfig.cropsitedbUrl+"/dataset/create").post(dsOpts).map { res =>
              val ret = (res.json).validate[CreateParams]
              ret match {
                case s: JsSuccess[CreateParams] => Ok(views.html.acewebui.uploadfiles(s.get.dsid))
                case e: JsError => BadRequest(JsError.toFlatJson(e).toString())
              }
            }
          }
          case None    => Future.successful(BadRequest("Missing Login Information"))
        }
      }
      )
  }
  def finalizeUpload = TODO
}
