lazy val commonSettings =
  Seq(scalaVersion := "2.12.6", organization := "com.example")

lazy val scalaReflect = Def.setting {
  "org.scala-lang" % "scala-reflect" % scalaVersion.value
}
lazy val root = (project in file(".")).settings(
  commonSettings,
  libraryDependencies ++= Seq(
    "io.getquill" %% "quill-core" % "3.5.1",
    "io.getquill" %% "quill-cassandra" % "3.5.1"
  )
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
