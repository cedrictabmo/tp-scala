package controllers

import akka.http.scaladsl.model.HttpHeader.ParsingResult.Ok
import akka.stream.Materializer
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.libs.json.Json
import play.api.mvc.{Action, EssentialAction}
import play.api.test._
import play.api.test.Helpers._

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 *
 * For more information, see https://www.playframework.com/documentation/latest/ScalaTestingWithScalaTest
 */
class HomeControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

  "HomeController GET" should {

    "render the index page from a new instance of controller" in {
      val controller = new HomeController(stubControllerComponents())
      val home = controller.index().apply(FakeRequest(GET, "/"))

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include ("Welcome to Play")
    }



    "Add a movie " in {

      implicit lazy val materializer: Materializer = app.materializer


      val controller = new HomeController (stubControllerComponents())
      val fakeJson = Json.parse("""{"title":"NOP","country":"FRA","year" : 1300, "french_release": "1300/02/02", "synopsis" : "bonchour", "genre":["dadada"], "ranking" : 5}""")

      val request = FakeRequest(POST, "/add").withJsonBody(fakeJson)
      val test = controller.addMovieToMedia().apply(request)

     status(test) mustEqual(OK)

    }

    "Add a movie title > 250 char" in {

      implicit lazy val materializer: Materializer = app.materializer


      val controller = new HomeController (stubControllerComponents())
      val fakeJson = Json.parse("""{"title":"PNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOPNOP","country":"FRA","year" : 1300, "french_release": "1300/02/02", "synopsis" : "bonchour", "genre":["dadada"], "ranking" : 5}""")

      val request = FakeRequest(POST, "/add").withJsonBody(fakeJson)
      val test = controller.addMovieToMedia()(request)

      status(test) mustEqual(400)


    }
    "Add a movie Bad Country" in {

      implicit lazy val materializer: Materializer = app.materializer


      val controller = new HomeController (stubControllerComponents())
      val fakeJson = Json.parse("""{"title":"NOP","country":"FRAKA","year" : 1300, "french_release": "1300/02/02", "synopsis" : "bonchour", "genre":["dadada"], "ranking" : 5}""")

      val request = FakeRequest(POST, "/add").withJsonBody(fakeJson)
      val test = controller.addMovieToMedia()(request)

      status(test) mustEqual(400)


    }

     "Add a movie already in" in {

    implicit lazy val materializer: Materializer = app.materializer


    val controller = new HomeController (stubControllerComponents())
    val fakeJson = Json.parse("""{"title":"NOP","country":"FRA","year" : 1300, "french_release": "1300/02/02", "synopsis" : "bonchour", "genre":["dadada"], "ranking" : 5}""")

    val request = FakeRequest(POST, "/add").withJsonBody(fakeJson)

       val test = controller.addMovieToMedia()(request) // on ajoute un film
       val test2 = controller.addMovieToMedia()(request) // on ajoute une 2e fois ce film
   status(test2) mustEqual(400)


  }

    "Mediatech empty" in {

      val controller = new HomeController(stubControllerComponents())
      val home = controller.mediatek.apply(FakeRequest(GET, "/look"))

      status(home) mustBe OK
      contentAsString(home) must include ("the mediatech is empty ... add some files !")

    }


    //ajout du film NOP
    "Mediatech not empty" in {

      implicit lazy val materializer: Materializer = app.materializer


      val controller = new HomeController (stubControllerComponents())
      val fakeJson = Json.parse("""{"title":"NOP","country":"FRA","year" : 1300, "french_release": "1300/02/02", "synopsis" : "bonchour", "genre":["dadada"], "ranking" : 5}""")

      val request = FakeRequest(POST, "/add").withJsonBody(fakeJson)

      val test = controller.addMovieToMedia()(request) // on ajoute un film

      val home = controller.mediatek.apply(FakeRequest(GET, "/look"))

      status(home) mustBe OK
      contentAsString(home) must include ("Our actual movie list : List(NOP  )")

    }

  }
}
