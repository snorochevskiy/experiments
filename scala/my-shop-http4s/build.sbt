lazy val Http4sVersion = "1.0.0-M29"
lazy val DoobieVersion = "1.0.0-M5"
lazy val H2Version = "2.1.214"
lazy val FlywayVersion = "9.2.0"
lazy val CirceVersion = "0.14.5"
lazy val PureConfigVersion = "0.17.4"
lazy val LogbackVersion = "1.2.11"

lazy val ScalaTestVersion = "3.2.16"
lazy val ScalaMockVersion = "5.2.0"

lazy val MunitVersion = "0.7.29"
lazy val MunitCatsEffectVersion = "1.0.6"

lazy val root = (project in file("."))
  .settings(
    organization := "me.snorochevskiy",
    name := "my-shop",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "3.2.0",
    libraryDependencies ++= Seq(
      "org.http4s"      %% "http4s-ember-server" % Http4sVersion,
      "org.http4s"      %% "http4s-ember-client" % Http4sVersion,
      "org.http4s"      %% "http4s-blaze-server"  % Http4sVersion,
      "org.http4s"      %% "http4s-circe"        % Http4sVersion,
      "org.http4s"      %% "http4s-dsl"          % Http4sVersion,

      "org.tpolecat" %% "doobie-core" % DoobieVersion,
      "org.tpolecat" %% "doobie-h2" % DoobieVersion,
      "org.tpolecat" %% "doobie-hikari" % DoobieVersion,

      "com.h2database" % "h2" % H2Version,

      "org.flywaydb" % "flyway-core" % FlywayVersion,

      "io.circe" %% "circe-generic" % CirceVersion,
      "io.circe" %% "circe-literal" % CirceVersion % "it,test",
      "io.circe" %% "circe-optics" % CirceVersion % "it",

      "com.github.pureconfig" %% "pureconfig-cats-effect" % PureConfigVersion,

      "org.scalameta"   %% "munit"               % MunitVersion           % Test,
      "org.typelevel"   %% "munit-cats-effect-3" % MunitCatsEffectVersion % Test,
      "ch.qos.logback"  %  "logback-classic"     % LogbackVersion,

      "org.scalatest" %% "scalatest" % ScalaTestVersion % Test
    ),
    testFrameworks += new TestFramework("munit.Framework")
  )
