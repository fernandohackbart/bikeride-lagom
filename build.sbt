organization in ThisBuild := "com.bikeride"
version in ThisBuild := "0.0.1-SNAPSHOT"
scalaVersion in ThisBuild := "2.11.8"

lagomCassandraCleanOnStart in ThisBuild := false
lagomCassandraEnabled in ThisBuild := false
//lagomUnmanagedServices in ThisBuild := Map("cas_native" -> "http://192.168.1.200:9042")
lagomUnmanagedServices in ThisBuild := Map("cas_native" -> "http://172.18.0.3:9042")

lagomKafkaEnabled in ThisBuild := false
//lagomKafkaAddress in ThisBuild := "192.168.1.200:9092"
lagomKafkaAddress in ThisBuild := "172.18.0.4:9092"

//#########################################################################
// http://www.scala-sbt.org/sbt-native-packager/formats/docker.html
enablePlugins(UniversalPlugin)
enablePlugins(DockerPlugin)
enablePlugins(JavaServerAppPackaging)

val macwire = "com.softwaremill.macwire" %% "macros" % "2.2.5" % "provided"
val jwt = "com.pauldijou" %% "jwt-play-json" % "0.14.0"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1" % Test
//val serviceLocatorDNS =  "com.lightbend" % "lagom13-scala-service-locator-dns_2.11" % "2.1.1"


//lazy val dockerSettings = Seq(packageName in Docker := "bikeride-backend",
//  packageName in Docker := "bikeride-backend",
//  //aggregate in Docker := true
//  packageSummary in Docker := "BikeRide Backend in Lagom",
//  packageDescription in Docker := "BikeRide Backend in Lagom",
//  version in Docker := "0.0.1",
//  maintainer in Docker := "Fernando Hackbart<fhackbart@gmail.com>",
//  dockerBaseImage := "bikeride/bikeride-backend:base",
//  daemonUser in Docker := "bikeride",
//  dockerExposedPorts := Seq(9000,8000,54610,61817,54431,49454),
//  //dockerExposedUdpPorts in Docker :=
//  //dockerExposedVolumes in Docker :=
//  //dockerLabels in Docker :=
//  dockerEntrypoint in Docker := Seq("/u01/bikeride/bin/bikeride-lagom"),
//  //defaultLinuxInstallLocation in Docker := "/u01/bikeride",
//  dockerRepository in Docker := Some("bikeride")
//  //dockerUsername in Docker :=
//  //dockerUpdateLatest in Docker :=
//  //dockerAlias in Docker :=
//  //dockerBuildOptions in Docker :=
//  //dockerExecCommand in Docker := Seq("sudo","/usr/bin/docker")
//  //dockerBuildCommand in Docker :=
//  //dockerRmiCommand in Docker :=
//  )


lazy val `bikeride-lagom` = (project in file("."))
  .aggregate(`authentication-lagom-api`,
    `authentication-lagom-impl`,
    `biker-lagom-api`,
    `biker-lagom-impl`,
    `track-lagom-api`,
    `track-lagom-impl`,
    `ride-lagom-api`,
    `ride-lagom-impl`,
    `utils`)

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
    ),
//    dockerSettings,
    lagomServicePort := 9010
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
    ),
//    dockerSettings,
    lagomServicePort := 9011
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
    ),
//    dockerSettings,
    lagomServicePort := 9012
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
    ),
//    dockerSettings,
    lagomServicePort := 9013
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`ride-lagom-api`,`track-lagom-api`,`biker-lagom-api`)

