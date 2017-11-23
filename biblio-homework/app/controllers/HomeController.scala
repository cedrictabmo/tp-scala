package controllers

import java.time.{LocalDate, LocalDateTime}
import javax.inject._

import play.api.libs.json._
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.mvc.{AbstractController, ControllerComponents}

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */


case class Movie(title: String, country: String, year: Int, originalTitle: Option[String],
                 frenchRelease: Option[String], synopsis: Option[String], genre: Seq[String], ranking: Int)

@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  // BD
  var lesFilms = Seq[Movie]()

  def existOrNot(mov: Movie): Boolean = {
    lesFilms.exists(x => x == mov)
  }

  // Liste des films
  def mediatek = {
    Action { request =>
      if (lesFilms.size == 0) Ok("the mediatech is empty ... add some files !") else Ok("Our actual movie list : " + lesFilms.distinct.map(x => x.title + " \n ").distinct)
    }
  }

  //classByGenre
  def classFilmByGenre(genre: String) = {
    Action { request =>
      if (genre.isEmpty) {
        Ok("Genre not specified, list of all movies : " + lesFilms.map(x => x.title + " "))
      }
      else {
        val filtered = lesFilms.map { x =>
          x.genre.map(y => if (y.toLowerCase == genre.toLowerCase) x.title else "")
        }
        Ok("Movie of the same Genre  : " + filtered.flatMap(x => x.distinct.filter(_ != "")) + " \n")
      }
    }
  }

  def countProductionYear(yearDate: Int) = {
    Action {
      request =>
        val counter = lesFilms.count(x => x.year == yearDate)
        Ok("there is  : " + counter + " Movie of the year " + yearDate + " \n")
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

        //verifie format + contenu
        def dateVerif(date : String) : Boolean = date.matches("^(\\d{4})-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3‌​[01])$")

        val form = Form(
          mapping(
            "title" -> nonEmptyText.verifying(maxLength(250)),
            "country" -> nonEmptyText,
            "year" -> number.verifying(min(1899), max(2018)),
            "original_title" -> optional(text.verifying(maxLength(250))),
            "french_release" -> optional(text.verifying(dateVerif _)),
            "synopsis" -> optional(text.verifying(maxLength(1000))),
            "genres" -> seq(text.verifying(maxLength(50))),
            "ranking" -> number.verifying(min(0), max(10))
          )(Movie.apply)(Movie.unapply _)
        )

        val boundForm = form.bind(json)

        if (existOrNot(mov) == false) {
          if (boundForm.errors.isEmpty) {
            lesFilms = lesFilms :+ mov
            Ok("Add of : " + mov + " to the mediatech \n")
          } else BadRequest("The movie : " + mov.title + " can't be add, Errors : " + boundForm.errors.map(x => x.message.toString + " \n"))
        } else BadRequest("Sorry but The movie : " + mov.title + " Already exist \n")
      }
    }
  }
}

