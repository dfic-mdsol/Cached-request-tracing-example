name := "cached-request-tracing-example"

version := "0.1"

scalaVersion := "2.12.6"
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.6.8",
  "com.typesafe.akka" %% "akka-stream" % "2.6.8",
  "com.typesafe.akka" %% "akka-http" % "10.1.12",
  "io.kamon" %% "kamon-zipkin" % "2.1.3",
  "io.kamon" %% "kamon-bundle" % "2.1.3",
  "com.github.cb372" %% "scalacache-caffeine" % "0.28.0"
)