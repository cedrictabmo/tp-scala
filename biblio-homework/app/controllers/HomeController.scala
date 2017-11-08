package controllers

import javax.inject._

import play.api.mvc._
import play.api.libs.json
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import sun.security.krb5.KrbException

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */


case class Movie(title: String,
                 country: String,
                 year: Int,
                 original_title: Option[String],
                 french_release: Option[String],
                 synopsis: Option[String],
                 genre: List[String],
                 ranking: Option[Int])


@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {


  var lesFilms = Seq[Movie]()

  def existOrNot(mov: Movie): Boolean = {

    lesFilms.exists(x => x == mov)
  }


  // Liste des films
  def mediatek = {
    Action { request =>
      if (lesFilms.size == 0) Ok("the mediatech is empty ... add some files !") else Ok("Our actual movie list : " + lesFilms.distinct.map(x => x.title + "  ").distinct)
    }
  }

  //classByGenre
  def classFilmByGenre(genre: String) = {
    Action { request =>
      var liste = List[String]()
      if (genre.isEmpty) {
        Ok("Genre no specified, list of all movies : " + lesFilms.map(x => x.title + " "))
      }
      else { // si genre pareil que autre film on recup le titre sinon non
        val test = lesFilms.map {
          x =>
            x.genre.map {
              y => if (y.toLowerCase == genre.toLowerCase) liste = liste :+ x.title else ""
            }
        }
      }
      Ok("Movie of the same Genre  : " + liste.distinct + " \n")
    }
  }

  def countProductionYear(yearDate: Int) = {
    Action {
      request =>
        var count = 0
        lesFilms.map(x => if (x.year == yearDate) count += 1)
        Ok("there is  : " + count + " Movie of the year " + yearDate + " \n")
    }
  }


  implicit def optionFormat[T: Format]: Format[Option[T]] = new Format[Option[T]] {
    override def reads(json: JsValue): JsResult[Option[T]] = json.validateOpt[T]

    override def writes(o: Option[T]): JsValue = o match {
      case Some(t) => implicitly[Writes[T]].writes(t)
      case None => JsNull
    }
  }

  // parse le film en json
  def addMovieToMedia = {

    Action {
      request => {

        implicit val movieWrite = Json.writes[Movie]
        implicit val movieReader = Json.reads[Movie]

        val json = request.body.asJson.get
        val mov = json.as[Movie]

        var errorMessage = List[String]()

        //title lenght
        if (mov.title.size > 250) errorMessage = errorMessage :+ "The title must contain 250 characters max "

        //country lenght
        if (mov.country.size > 3) errorMessage = errorMessage :+ "The nationality need contain only 3 characters "

        mov.french_release.getOrElse("") match {
          case x => {
            try {
              val mich = org.joda.time.format.DateTimeFormat.forPattern("yyyy/MM/dd")
              val ouv = Some(mich.parseDateTime(x))
            } catch {
              case e: Exception => None
                errorMessage = errorMessage :+ "Dateformat must be like yyyy/MM/dd "
            }
          }
        }


        if (mov.genre.isEmpty) errorMessage = errorMessage :+ "Genre list is empty, Add at least 1 genre plz "
        mov.genre.map(x => if (x.size > 50) errorMessage = errorMessage :+ "the lenght of each genre must contain 50 characters max ")

        if (mov.country == "FRA" && mov.original_title.isEmpty) Ok else if (mov.country != "FRA" && mov.original_title.isEmpty) errorMessage = errorMessage :+ "if country != 'FRA', it can't be empty .. "
        if (mov.original_title.size > 250) errorMessage = errorMessage :+ "The original_title need contain 250 characters max, limit reached "


        //ranking range
        mov.ranking.getOrElse(Ok) match {
          case x: Int => if (x >= 0 || x <= 10) Ok
          case Ok => Ok
          case _ => errorMessage = errorMessage :+ "The ranking need to be between 0 and 10 with entier value"
        }

        val bool = existOrNot(mov)
        if (bool == false) {
          if (errorMessage.isEmpty) {
            lesFilms = lesFilms :+ mov;
          }
        } else errorMessage = errorMessage :+ "there is already a movie with this name sorry "
        if (errorMessage.isEmpty) Ok("Ajout du film suivant : " + mov + " \n") else BadRequest("Le film : " + mov.title + " n'a pas pu être ajouté, voici la liste des erreurs : " + errorMessage.map(x => x).distinct + " \n")

      }
    }
  }

  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }
}


