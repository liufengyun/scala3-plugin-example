val dottyVersion = "0.27.0-RC1"

lazy val plugin = project
  .settings(
    name := "scala-instrumentation-plugin",
    version := "0.1.0",

    scalaVersion := dottyVersion,

    libraryDependencies += "ch.epfl.lamp" %% "dotty-compiler" % dottyVersion % "provided",
    libraryDependencies += "org.scalameta" %% "munit" % "0.7.15" % Test,
    testFrameworks += new TestFramework("munit.Framework")
  )

lazy val runtime = project
  .settings(
    name := "scala-instrumentation-runtime",
    version := "0.1.0",

    scalaVersion := dottyVersion,

    libraryDependencies += "org.scalameta" %% "munit" % "0.7.15" % Test,
    testFrameworks += new TestFramework("munit.Framework")
  )

lazy val root = project
  .aggregate(plugin, runtime)
