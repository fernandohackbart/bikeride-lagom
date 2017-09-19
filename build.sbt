organization in ThisBuild := "com.bikeride"
version in ThisBuild := "1.0-SNAPSHOT"
scalaVersion in ThisBuild := "2.11.8"

val macwire = "com.softwaremill.macwire" %% "macros" % "2.2.5" % "provided"
val jwt = "com.pauldijou" %% "jwt-play-json" % "0.14.0"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1" % Test

lazy val `bikeride-lagom` = (project in file("."))
  .aggregate(`biker-lagom-api`, `biker-lagom-impl`,`track-lagom-api`,`track-lagom-impl`)

lazy val utils = (project in file("utils"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi,
      lagomScaladslServer % Optional,
      scalaTest,
      jwt
    )
  )

lazy val `biker-lagom-api` = (project in file("biker-lagom-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )
  .dependsOn(`utils`)

lazy val `biker-lagom-impl` = (project in file("biker-lagom-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`biker-lagom-api`,`utils`)

lazy val `track-lagom-api` = (project in file("track-lagom-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )
  .dependsOn(`utils`)

lazy val `track-lagom-impl` = (project in file("track-lagom-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`track-lagom-api`,`biker-lagom-api`,`utils`)


lazy val `ride-lagom-api` = (project in file("ride-lagom-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )
  .dependsOn(`utils`)
  .dependsOn(`biker-lagom-api`)
  .dependsOn(`track-lagom-api`)

/*
lazy val `ride-lagom-impl` = (project in file("ride-lagom-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`ride-lagom-api`,`track-lagom-api`,`biker-lagom-api`)
*/

lagomCassandraCleanOnStart in ThisBuild := true