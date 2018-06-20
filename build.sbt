name := """play-scala-fileuploader"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.11"

libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" % Test
libraryDependencies += ws

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
