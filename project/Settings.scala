import sbt._
import sbt.Keys.scalaVersion

object Settings {
  lazy val scalaReflect = Def.setting {
    "org.scala-lang" % "scala-reflect" % scalaVersion.value
  }
}
