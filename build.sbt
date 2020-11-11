val dottyVersion = "0.27.0-RC1"

lazy val plugin = project
  .settings(
    name := "scala-instrumentation-plugin",
    organization := "xmid.org",
    version := "0.1.0",

    scalaVersion := dottyVersion,

    libraryDependencies += "ch.epfl.lamp" %% "dotty-compiler" % dottyVersion % "provided",
    libraryDependencies += "org.scalameta" %% "munit" % "0.7.15" % Test,
    testFrameworks += new TestFramework("munit.Framework")
  )

lazy val runtime = project
  .settings(
    name := "scala-instrumentation-runtime",
    organization := "xmid.org",
    version := "0.1.0",

    scalaVersion := dottyVersion,

    libraryDependencies += "org.scalameta" %% "munit" % "0.7.15" % Test,
    testFrameworks += new TestFramework("munit.Framework")
  )

lazy val hello = project
  .settings(
    name := "hello",
    version := "0.1.0",
    scalaVersion := dottyVersion,

    libraryDependencies += "xmid.org" %% "scala-instrumentation-runtime" % "0.1.0",
    libraryDependencies += compilerPlugin("xmid.org" %% "scala-instrumentation-plugin" % "0.1.0")
    // scalacOptions += "-P:instrumentation:path/to/config"
  )


lazy val root = project
  .aggregate(plugin, runtime)
