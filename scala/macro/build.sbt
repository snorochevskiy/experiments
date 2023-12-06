ThisBuild / name := "macros-test"
ThisBuild / organization := "com.github.snorochevskiy"
ThisBuild / version := "1.0.0"

scalaVersion in ThisBuild := "2.12.8"

// Macro methods should be defined in separate module
lazy val macros = (project in file("macros")).settings(
  libraryDependencies ++= Seq(
    "org.scala-lang" % "scala-reflect" % scalaVersion.value
  )
)

lazy val app = (project in file("app")).dependsOn(macros)
.settings(
  mainClass in (Compile, run) := Some("demo.usemacro.MacrosTestApp")
)

// Workaround to call "run" on "mainClass" class from app.
// Just don't know how to forward "sbt run" to "sbt app/run" in other way.
lazy val root = (project in file(".")).dependsOn(app)
.settings(
  mainClass in (Compile, run) := (app / Compile / mainClass).value
)

