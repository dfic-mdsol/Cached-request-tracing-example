import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.server.Directives._
import akka.stream.Materializer
import kamon.Kamon
import kamon.trace.Span
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
        for {
          _ <-  scalacache.caching[Id, Future[HttpResponse]]("key")(Some(30 seconds)) {
            Http().singleRequest(HttpRequest(uri = "http://google.com")).flatMap(_.toStrict(5 seconds))
          }.map(_.status.toString)
          ctx = Kamon.currentContext()
        _ = println(s"TraceID: ${ctx.entries.toSeq.head.value.asInstanceOf[Span.Local].trace.id.string}")
        a <- Http().singleRequest(HttpRequest(uri = "http://google.com")).map(_.status.toString)
        } yield a
      }
    }
  }

  Http().bindAndHandle(route, "0.0.0.0", 8080)

}