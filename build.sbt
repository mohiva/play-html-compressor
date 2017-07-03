import com.typesafe.sbt.SbtScalariform._
import play.sbt.PlayImport._
import xerial.sbt.Sonatype._

import scalariform.formatter.preferences._

//*******************************
// Play settings
//*******************************

name := "play-html-compressor"

version := "0.7.1"

libraryDependencies ++= Seq(
  "com.googlecode.htmlcompressor" % "htmlcompressor" % "1.5.2",
  "rhino" % "js" % "1.7R2",
  "org.easytesting" % "fest-assert" % "1.4" % Test,
  specs2 % Test,
  javaCore % Test,
  filters % Test
)

lazy val root = (project in file(".")).enablePlugins(play.sbt.Play)

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

//*******************************
// Compiler settings
//*******************************

scalaVersion := "2.12.2"

crossScalaVersions := Seq("2.12.2", "2.11.11")

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

// Allow dead code in tests (to support using mockito).
scalacOptions in Test ~= { (options: Seq[String]) => options filterNot ( _ == "-Ywarn-dead-code" ) }

javacOptions ++= Seq(
  "-Xlint:deprecation"
)

//*******************************
// Scalariform settings
//*******************************

defaultScalariformSettings

ScalariformKeys.preferences := ScalariformKeys.preferences.value
  .setPreference(FormatXml, false)
  .setPreference(DoubleIndentClassDeclaration, false)
  .setPreference(DanglingCloseParenthesis, Preserve)
