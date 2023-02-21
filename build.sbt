lazy val shamir = project
  .in(file("."))
  .settings(
    name := "shamir",
    version := "current",
    scalaVersion := "3.2.1",
    scalacOptions ++= Seq("-deprecation", "-unchecked")
  )
