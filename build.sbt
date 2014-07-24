import play.PlayScala
import com.typesafe.sbt.SbtScalariform._
import xerial.sbt.Sonatype._

//*******************************
// Play settings
//*******************************

name := "play-html-compressor"

version := "0.3.1"

libraryDependencies ++= Seq(
  "com.googlecode.htmlcompressor" % "htmlcompressor" % "1.5.2",
  "rhino" % "js" % "1.7R2"
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)

//*******************************
// Maven settings
//*******************************

sonatypeSettings

organization := "com.mohiva"

description := "Google's HTML Compressor for Play Framework 2"

homepage := Some(url("https://github.com/mohiva/play-html-compressor/"))

licenses := Seq("BSD New" -> url("https://github.com/mohiva/play-html-compressor/blob/master/LICENSE.md"))

val pom = <scm>
    <url>git@github.com:mohiva/play-html-compressor.git</url>
    <connection>scm:git:git@github.com:mohiva/play-html-compressor.git</connection>
  </scm>
  <developers>
    <developer>
      <id>akkie</id>
      <name>Christian Kaps</name>
      <url>http://mohiva.com</url>
    </developer>
  </developers>

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := pom

credentials += Credentials(Path.userHome / ".sbt" / "sonatype.credentials")

//*******************************
// Test settings
//*******************************

parallelExecution in Test := false

//*******************************
// Compiler settings
//*******************************

scalaVersion := "2.11.1"

crossScalaVersions := Seq("2.10.4", "2.11.0")

scalacOptions ++= Seq(
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  "-unchecked", // Enable additional warnings where generated code depends on assumptions.
  "-Xfatal-warnings", // Fail the compilation if there are any warnings.
  "-Xlint", // Enable recommended additional warnings.
  "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver.
  "-Ywarn-dead-code", // Warn when dead code is identified.
  "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
  "-Ywarn-nullary-override", // Warn when non-nullary overrides nullary, e.g. def foo() over def foo.
  "-Ywarn-numeric-widen" // Warn when numerics are widened.
)

scalacOptions in Test ~= { (options: Seq[String]) =>
  options filterNot ( _ == "-Ywarn-dead-code" )  // Allow dead code in tests (to support using mockito).
}

//*******************************
// Scalariform settings
//*******************************

defaultScalariformSettings
