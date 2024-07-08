ThisBuild / scalaVersion := "3.4.2"

lazy val root = (project in file("."))
  .settings(
    name := "ants",
    scalacOptions ++= Seq("-deprecation", "-explain", "-feature")
  )
