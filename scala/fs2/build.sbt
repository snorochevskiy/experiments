
scalaVersion := "2.13.3"

name := "playing-fs2"
organization := "snorochevskiy"
version := "1.0"

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2",

  "co.fs2" %% "fs2-core" % "3.1.0",
  "co.fs2" %% "fs2-io" % "3.1.0",
  "co.fs2" %% "fs2-reactive-streams" % "3.1.0"
)
