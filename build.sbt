name := "Smart Process API - PlayApp"

version := "0.0.3"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs,
  "edu.stanford.protege" % "org.protege.editor.owl.codegeneration" % "1.0.2",
  "net.sourceforge.owlapi" % "owlapi-distribution" % "3.5.0",
  "mysql" % "mysql-connector-java" % "5.1.18",
  "org.apache.directory.studio" % "org.apache.commons.io" % "2.4",
  "org.webjars" %% "webjars-play" % "2.3.0-2",
  "org.webjars" % "webjars-locator" % "0.26",
  "org.webjars" % "requirejs" % "2.1.18",
  "org.webjars" % "jquery-ui" % "1.11.4",
  "org.webjars" % "datatables" % "1.10.7",
  "org.webjars" % "bpmn-js" % "0.10.3",
  "org.webjars" % "chosen" % "1.3.0"
)

fork in run := true