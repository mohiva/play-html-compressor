// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.2.1")

// Use the PGP plugin to sign the artefacts
// http://www.scala-sbt.org/sbt-pgp/
addSbtPlugin("com.typesafe.sbt" % "sbt-pgp" % "0.8.1")
