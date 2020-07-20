import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.server.Directives._
import akka.stream.Materializer
import kamon.Kamon
import scalacache.{CacheConfig, Flags, Id, cachingF}
import scalacache.caffeine.CaffeineCache
import scalacache.modes.sync._
import scala.concurrent.Future
import scala.concurrent.duration._

object Server extends App {
  Kamon.init()
  implicit val system = ActorSystem("helloworld")
  implicit val executor = system.dispatcher
  implicit val materializer = Materializer(system)
  implicit val flags = Flags(readsEnabled = true, writesEnabled = true)
  implicit val httpResponseCache = CaffeineCache[Future[HttpResponse]](CacheConfig())

  def route = path("hello") {
    get {
      complete {
        scalacache.caching[Id, Future[HttpResponse]]("key")(Some(30 seconds)) {
          Http().singleRequest(HttpRequest(uri = "http://akka.io"))
        }.map(_.status.intValue.toString)
      }
    }
  }

  Http().bindAndHandle(route, "0.0.0.0", 8080)

}