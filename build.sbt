ThisBuild / scalaVersion := "3.1.3"

val libVersion = "0.1.0"
val org = "org.mycompany"

lazy val plugin = project
  .settings(
    name := "scala-counter-plugin",
    organization := org,
    version := libVersion,

    libraryDependencies += "org.scala-lang" %% "scala3-compiler" % scalaVersion.value % "provided"
  )

lazy val runtime = project
  .settings(
    name := "scala-counter-runtime",
    organization := "org.mycompany",
    version := libVersion
  )

lazy val counter = project
  .aggregate(plugin, runtime)
  .settings(
    name := "scala-counter",
    organization := org,
    version := libVersion
  )


lazy val hello = project
  .settings(
    name := "hello",
    version := "0.1.0",

    scalacOptions += "-P:counter:hello/counter.yml",

    libraryDependencies += "org.mycompany" %% "scala-counter-runtime" % "0.1.0",
    libraryDependencies += compilerPlugin("org.mycompany" %% "scala-counter-plugin" % "0.1.0")
  )


lazy val root = project
  .aggregate(plugin, runtime)
