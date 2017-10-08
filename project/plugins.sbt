//For the DNS locator
resolvers += Resolver.bintrayRepo("hajile", "maven")
resolvers += Resolver.typesafeRepo("releases")

// The Lagom plugin
addSbtPlugin("com.lightbend.lagom" % "lagom-sbt-plugin" % "1.3.8")
// Needed for importing the project into Eclipse
addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "5.1.0")
// To build the Docker container
//addSbtPlugin("com.spotify" % "docker-client" % "8.9.1")
//libraryDependencies += "com.spotify" % "docker-client" % "3.5.13"