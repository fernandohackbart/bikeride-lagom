resolvers += Resolver.typesafeRepo("releases")

addSbtPlugin("de.heikoseeberger" % "sbt-header"      % "1.5.1")
addSbtPlugin("com.jsuereth"      % "sbt-pgp"         % "1.0.0")
addSbtPlugin("com.github.gseitz" % "sbt-release"     % "1.0.3")
addSbtPlugin("org.xerial.sbt"    % "sbt-sonatype"    % "1.1")

// The Lagom plugin
addSbtPlugin("com.lightbend.lagom" % "lagom-sbt-plugin" % "1.3.8")
// Needed for importing the project into Eclipse
addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "5.1.0")
// To build the Docker container
//addSbtPlugin("com.spotify" % "docker-client" % "8.9.1")
//libraryDependencies += "com.spotify" % "docker-client" % "3.5.13"