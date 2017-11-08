package controllers

import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.{Inject, Singleton}
import play.api.mvc.{Action, Controller}
import com.github.nscala_time.time.Imports._
import org.joda.time.{Months}
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.libs.ws.{WSClient}


@Singleton
class HomeController @Inject()(ws: WSClient) extends Controller {


  def sortByAuthors(elements: Seq[String]): Seq[String] = {
    elements
      .groupBy(identity)
      .mapValues(_.size)
      .toList
      .sortWith((x, y) => x._2 > y._2)
      .take(10)
      .flatMap(x => Seq.apply(s"user : ${x._1} with actually : ${x._2} commits"))
  }


  def sortByDate(elements: Seq[String]): Map[String, Int] = {
    elements.groupBy(identity).mapValues(_.size).toList.sortWith((x, y) => x._1 < y._1).toMap
  }


  def getCommits(user: String, repo: String) = Action.async {
    val url = s"https://api.github.com/repos/$user/$repo/commits"
    ws.url(url).get().map { response =>
      if (response.status == 200) {
        val body: JsValue = response.json
        val authors = (body \\ "author").map { author =>
          (author \ "login").asOpt[String].getOrElse("unknown")
        }
        val authorsSorted: Seq[String] = sortByAuthors(authors)
        Ok(Json.toJson(authorsSorted))
      } else {
        BadRequest("Invalid response from GIT")
      }
    }
  }


  def getMostLanguagesUsed(user: String, repo: String) = Action.async {
    val url = s"https://api.github.com/repos/$user/$repo/languages"
    ws.url(url).get().map { response =>
      if (response.status == 200) {
        val body: JsValue = response.json
        val top = body.as[JsObject].value.mapValues(_.as[Int]).toList.sortWith((x, y) => x._2 > y._2).take(10)
        Ok(Json.toJson(top))
      } else {
        BadRequest("Invalid response from GIT")
      }
    }
  }


  def isBetween(date: String) = {
    val actual = DateTime.now
    val daysAgo = DateTime.now - 1.month
    val converted = DateTime.parse(date.substring(0, 10))
    val months = Months.monthsBetween(actual, converted)
    val isSup = Months.monthsBetween(actual, daysAgo)

    if (months == isSup) true else false
  }

  var dates = List[String]()

  def getThirtyDays(day: DateTime): List[String] = {
    val startDate = day.minusDays(1)
    dates = dates :+ startDate.toString.substring(0, 10)
    dates.size match {
      case 30 => dates
      case _ => getThirtyDays(startDate)
    }
  }


  def getIssues(user: String, repo: String) = Action.async {
    val url = s"https://api.github.com/repos/$user/$repo/issues"
    var ll: List[(String, Int)] = Nil
    
    ws.url(url).get().map { response =>
      if (response.status == 200) {
        val body: JsValue = response.json
        val issuesDate: Seq[String] = (body \\ "created_at").map(date => date.as[String])
        val dates: List[String] = getThirtyDays(DateTime.now)
        val issuesSorted = sortByDate(issuesDate)
        issuesSorted.map { x =>
          isBetween(x._1) match {
            case true => ll = ll :+ x
            case false => "Nop "
          }
        }
        val finalList = ll.flatMap { case (str, int) =>
          dates.map(d => if (d.equals(str.substring(0, 10))) (d, int) else (d, 0))
        }
        Ok("" + finalList.map(e => e + "\n"))
      } else {
        BadRequest("Invalid response from GIT")
      }
    }
  }
}
