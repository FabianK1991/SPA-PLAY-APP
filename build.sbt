name := "Smart Process API - PlayApp"

version := "0.0.2"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs,
  "org.webjars" %% "webjars-play" % "2.3.0-2",
  "org.webjars" % "webjars-locator" % "0.17",
  "org.webjars" % "requirejs" % "2.1.16",
  "org.webjars" % "jquery-ui" % "1.11.3",
  "org.webjars" % "bpmn-js" % "0.8.0",
  "org.apache.directory.studio" % "org.apache.commons.io" % "2.4"
)