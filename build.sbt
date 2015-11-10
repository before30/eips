name := "eips"

version := "1.0"

scalaVersion := "2.11.7"

resolvers ++= {
  Seq(
    "repo" at "http://repo.typesafe.com/typesafe/releases/"
  )
}

libraryDependencies ++= {
  val akkaVersion = "2.3.9"

  Seq(
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
    "com.typesafe.akka" %% "akka-actor" % akkaVersion
  )
}