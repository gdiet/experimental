ThisBuild / scalaVersion := "3.4.2"

lazy val root = (project in file("."))
  .settings(
    name := "ants",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19" % "test",
    scalacOptions ++= Seq("-deprecation", "-explain", "-feature"),
  )
