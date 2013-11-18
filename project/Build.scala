import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "play-html-compressor"
  val appVersion      = "0.2-SNAPSHOT"

  val appDependencies = Seq(
    "com.googlecode.htmlcompressor" % "htmlcompressor" % "1.5.2",
    "rhino" % "js" % "1.7R2"
  )

  val pom =
    <scm>
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
  ;

  val main = play.Project(appName, appVersion, appDependencies).settings(
    organization := "com.mohiva",
    description := "Google's HTML Compressor for Play Framework 2.1",
    homepage := Some(url("https://github.com/mohiva/play-html-compressor")),
    licenses := Seq("BSD New" -> url("https://github.com/mohiva/play-html-compressor/blob/master/LICENSE.md")),
    publishTo <<= version { v: String =>
    val nexus = "https://oss.sonatype.org/"
    if (v.trim.endsWith("SNAPSHOT"))
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    publishMavenStyle := true,
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => false },
    pomExtra := pom,
    credentials += Credentials(Path.userHome / ".sbt" / "sonatype.credentials")
  )
}
