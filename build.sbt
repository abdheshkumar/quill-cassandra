lazy val commonSettings = Seq(
  scalaVersion := "2.12.2",
  organization := "com.example"
)

lazy val scalaReflect = Def.setting {
  "org.scala-lang" % "scala-reflect" % scalaVersion.value
}
lazy val root = (project in file(".")).settings(
  commonSettings,
  libraryDependencies += "io.getquill" % "quill-cassandra_2.12" % "1.4.0"
)

lazy val core = (project in file("core"))
  .dependsOn(macroSub, util)
  .settings(
    commonSettings
    // other settings here
  )

lazy val macroSub = (project in file("macro"))
  .dependsOn(util)
  .settings(
    commonSettings,
    libraryDependencies += scalaReflect.value
    // other settings here
  )

lazy val util = (project in file("util"))
  .settings(
    commonSettings
    // other setting here
  )