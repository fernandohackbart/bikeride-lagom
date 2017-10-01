organization in ThisBuild := "com.bikeride"
version in ThisBuild := "0.0.1-SNAPSHOT"
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

lazy val `authentication-lagom-api` = (project in file("authentication-lagom-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )
  .dependsOn(`utils`,`biker-lagom-api`)

lazy val `authentication-lagom-impl` = (project in file("authentication-lagom-impl"))
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
  .dependsOn(`authentication-lagom-api`,`biker-lagom-api`,`utils`)

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
  .dependsOn(`utils`,`biker-lagom-api`,`track-lagom-api`)


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

lagomCassandraCleanOnStart in ThisBuild := true
lagomCassandraEnabled in ThisBuild := false
lagomUnmanagedServices in ThisBuild := Map("cas_native" -> "http://localhost:9042")

lagomKafkaEnabled in ThisBuild := false
lagomKafkaAddress in ThisBuild := "localhost:9092"

//#########################################################################
// http://www.scala-sbt.org/sbt-native-packager/formats/docker.html
enablePlugins(DockerPlugin)
//#########################################################################
//Informational Settings
packageName in Docker := "bikeride-backend"
packageSummary in Docker := "BikeRide Backend in Lagom"
packageDescription := "BikeRide Backend in Lagom"
version in Docker := "0.0.1"
maintainer in Docker := "Fernando Hackbart<fhackbart@gmail.com>"

//Environment Settings
dockerBaseImage := "bikeride/bikeride-backend:base"
daemonUser in Docker := "bikeride"
dockerExposedPorts := Seq(9000,8000,54610,61817,54431,49454)
//dockerExposedUdpPorts :=
//dockerExposedVolumes :=
//dockerLabels :=
//dockerEntrypoint :=
defaultLinuxInstallLocation in Docker := "/u01/bikeride-lagom"

//Publishing Settings
dockerRepository := Some("bikeride")
//dockerUsername :=
//dockerUpdateLatest :=
//dockerAlias :=
//dockerBuildOptions :=
//dockerExecCommand := Seq("sudo","/usr/bin/docker")
//dockerBuildCommand :=
//dockerRmiCommand :=






