organization in ThisBuild := "com.bikeride"
version in ThisBuild := "0.0.1-SNAPSHOT"
scalaVersion in ThisBuild := "2.11.12"
//#########################################################################
lagomCassandraCleanOnStart in ThisBuild := false
lagomCassandraEnabled in ThisBuild := true
lagomKafkaEnabled in ThisBuild := true
//#########################################################################
val macwire = "com.softwaremill.macwire" %% "macros" % "2.2.5" % "provided"
val jwt = "com.pauldijou" %% "jwt-play-json" % "0.14.0"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1" % Test
val serviceLocatorDNS = "com.lightbend" %% "lagom13-scala-service-locator-dns" % "2.2.2"
val srvDNSakkaDns = "ru.smslv.akka" %% "akka-dns" % "2.4.2"
//#########################################################################
lazy val `bikeride-lagom` = (project in file("."))
  .aggregate(`authentication-lagom-api`,
    `authentication-lagom-impl`,
    `biker-lagom-api`,
    `biker-lagom-impl`,
    `track-lagom-api`,
    `track-lagom-impl`,
    `ride-lagom-api`,
    `ride-lagom-impl`,
    `analytics-lagom-api`,
    `analytics-lagom-impl`,
    `utils`)
//#########################################################################
lazy val utils = (project in file("utils"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi,
      lagomScaladslServer % Optional,
      scalaTest,
      jwt
    ),
    resolvers += Resolver.jcenterRepo
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
    dockerUpdateLatest := false,
    dockerUsername := Some("bikeride"),
    dockerExposedPorts := Seq(9000,2551),
    dockerEntrypoint ++= """ $(eval "echo $SERVICE_DEBUG") -Dhttp.address="$(eval "echo $SERVICE_BIND_IP")" -Dhttp.port="$(eval "echo $EVENTINGESTIONSERVICE_BIND_PORT")" -Dakka.remote.netty.tcp.hostname="$(eval "echo $AKKA_REMOTING_HOST")" -Dakka.remote.netty.tcp.bind-hostname="$(eval "echo $AKKA_REMOTING_BIND_HOST")" -Dakka.remote.netty.tcp.port="$(eval "echo $AKKA_REMOTING_PORT")" -Dakka.remote.netty.tcp.bind-port="$(eval "echo $AKKA_REMOTING_BIND_PORT")" $(IFS=','; I=0; for NODE in $AKKA_SEED_NODES; do echo "-Dakka.cluster.seed-nodes.$I=akka.tcp://bikeride-authentication@$NODE"; I=$(expr $I + 1); done)""".split(" ").toSeq,
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      serviceLocatorDNS,
      macwire,
      scalaTest
    ),
    resolvers += Resolver.jcenterRepo
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
    dockerUpdateLatest := false,
    dockerUsername := Some("bikeride"),
    dockerExposedPorts := Seq(9000,2551),
    dockerEntrypoint ++= """ $(eval "echo $SERVICE_DEBUG") -Dhttp.address="$(eval "echo $SERVICE_BIND_IP")" -Dhttp.port="$(eval "echo $EVENTINGESTIONSERVICE_BIND_PORT")" -Dakka.remote.netty.tcp.hostname="$(eval "echo $AKKA_REMOTING_HOST")" -Dakka.remote.netty.tcp.bind-hostname="$(eval "echo $AKKA_REMOTING_BIND_HOST")" -Dakka.remote.netty.tcp.port="$(eval "echo $AKKA_REMOTING_PORT")" -Dakka.remote.netty.tcp.bind-port="$(eval "echo $AKKA_REMOTING_BIND_PORT")" $(IFS=','; I=0; for NODE in $AKKA_SEED_NODES; do echo "-Dakka.cluster.seed-nodes.$I=akka.tcp://bikeride-biker@$NODE"; I=$(expr $I + 1); done)""".split(" ").toSeq,
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      serviceLocatorDNS,
      macwire,
      scalaTest
    ),
    resolvers += Resolver.jcenterRepo
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
    dockerUpdateLatest := false,
    dockerUsername := Some("bikeride"),
    dockerExposedPorts := Seq(9000,2551),
    dockerEntrypoint ++= """ $(eval "echo $SERVICE_DEBUG") -Dhttp.address="$(eval "echo $SERVICE_BIND_IP")" -Dhttp.port="$(eval "echo $EVENTINGESTIONSERVICE_BIND_PORT")" -Dakka.remote.netty.tcp.hostname="$(eval "echo $AKKA_REMOTING_HOST")" -Dakka.remote.netty.tcp.bind-hostname="$(eval "echo $AKKA_REMOTING_BIND_HOST")" -Dakka.remote.netty.tcp.port="$(eval "echo $AKKA_REMOTING_PORT")" -Dakka.remote.netty.tcp.bind-port="$(eval "echo $AKKA_REMOTING_BIND_PORT")" $(IFS=','; I=0; for NODE in $AKKA_SEED_NODES; do echo "-Dakka.cluster.seed-nodes.$I=akka.tcp://bikeride-track@$NODE"; I=$(expr $I + 1); done)""".split(" ").toSeq,
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      serviceLocatorDNS,
      macwire,
      scalaTest
    ),
    resolvers += Resolver.jcenterRepo
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
    dockerUpdateLatest := false,
    dockerUsername := Some("bikeride"),
    dockerExposedPorts := Seq(9000,2551),
    dockerEntrypoint ++= """ $(eval "echo $SERVICE_DEBUG") -Dhttp.address="$(eval "echo $SERVICE_BIND_IP")" -Dhttp.port="$(eval "echo $EVENTINGESTIONSERVICE_BIND_PORT")" -Dakka.remote.netty.tcp.hostname="$(eval "echo $AKKA_REMOTING_HOST")" -Dakka.remote.netty.tcp.bind-hostname="$(eval "echo $AKKA_REMOTING_BIND_HOST")" -Dakka.remote.netty.tcp.port="$(eval "echo $AKKA_REMOTING_PORT")" -Dakka.remote.netty.tcp.bind-port="$(eval "echo $AKKA_REMOTING_BIND_PORT")" $(IFS=','; I=0; for NODE in $AKKA_SEED_NODES; do echo "-Dakka.cluster.seed-nodes.$I=akka.tcp://bikeride-ride@$NODE"; I=$(expr $I + 1); done)""".split(" ").toSeq,
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      serviceLocatorDNS,
      macwire,
      scalaTest
    ),
    resolvers += Resolver.jcenterRepo
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`ride-lagom-api`,`track-lagom-api`,`biker-lagom-api`)

lazy val `analytics-lagom-api` = (project in file("analytics-lagom-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )
  .dependsOn(`utils`)

lazy val `analytics-lagom-impl` = (project in file("analytics-lagom-impl"))
  .enablePlugins(LagomScala)
  .settings(
    //dockerRepository := Some(BuildTarget.dockerRepository),
    dockerUpdateLatest := false,
    dockerUsername := Some("bikeride"),
    dockerExposedPorts := Seq(9000,2551),
    dockerEntrypoint ++= """ $(eval "echo $SERVICE_DEBUG") -Dhttp.address="$(eval "echo $SERVICE_BIND_IP")" -Dhttp.port="$(eval "echo $EVENTINGESTIONSERVICE_BIND_PORT")" -Dakka.remote.netty.tcp.hostname="$(eval "echo $AKKA_REMOTING_HOST")" -Dakka.remote.netty.tcp.bind-hostname="$(eval "echo $AKKA_REMOTING_BIND_HOST")" -Dakka.remote.netty.tcp.port="$(eval "echo $AKKA_REMOTING_PORT")" -Dakka.remote.netty.tcp.bind-port="$(eval "echo $AKKA_REMOTING_BIND_PORT")" $(IFS=','; I=0; for NODE in $AKKA_SEED_NODES; do echo "-Dakka.cluster.seed-nodes.$I=akka.tcp://bikeride-analytics@$NODE"; I=$(expr $I + 1); done)""".split(" ").toSeq,
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      serviceLocatorDNS,
      macwire,
      scalaTest
    ),
    resolvers += Resolver.jcenterRepo
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`analytics-lagom-api`,`authentication-lagom-api`)