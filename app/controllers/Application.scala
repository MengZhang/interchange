package interchange.controllers

import play.api._
import play.api.mvc._

import interchange.util.ProfileAction

object Application extends Controller {

  def index = ProfileAction { implicit request =>
    Ok(views.html.index())
  }

}
