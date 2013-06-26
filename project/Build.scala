import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "play-html-compressor"
  val appVersion      = "0.1-SNAPSHOT"

  val appDependencies = Seq()

  val main = play.Project(appName, appVersion, appDependencies).settings(
    libraryDependencies += "com.googlecode.htmlcompressor" % "htmlcompressor" % "1.5.2",
    libraryDependencies += "rhino" % "js" % "1.7R2"
  )
}
