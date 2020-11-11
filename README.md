# Scala 3 Instrumentation

A Scala 3 compiler plugin for instrumentation.

## Usage

First, clone the repo and publish the plugin locally:

```
sbt > plugin/publishLocal
```

Then enable the plugin in your SBT build:

``` scala
libraryDependencies += "xmid.org" %% "scala-instrumentation-runtime" % "0.1.0",

libraryDependencies += compilerPlugin("xmid.org" %% "scala-instrumentation-plugin" % "0.1.0")
```

Now compile your program, a file named `methods.csv` will be generated:

``` bash
# id, method, class, top-level class, file, line
0, main, Hello$, Hello$, hello/Hello.scala, 1
1, foo, Hello$, Hello$, Hello.scala, 5
2, bar, Hello$, Hello$, Hello.scala, 10
```

Run your program with some sample input, a file named `results.csv` will be generated:

``` bash
# id, calls, accumulated calls
0, 1, 8
1, 6, 7
2, 1, 1
```

You can use standard tools like [xsv](https://github.com/BurntSushi/xsv) to join the two files:

``` bash
xsv join 1 methods.csv 1 results.csv  | xsv table    # pretty print
xsv join 1 methods.csv 1 results.csv  > joined.csv   # for input to spreadsheet
```

You can also supply a config file to the plugin in the SBT build:

``` scala
scalacOptions += "-P:instrumenter:hello/instrument.yml"
```

The config file has the following format:

``` yml
methodsCSV: hello/methods.csv
resultsCSV: hello/results.csv
```

Please check the configuration for the subproject `hello` in
[build.sbt](build.sbt) for more detail.

## Development

First, publish the plugin locally:

```
sbt > plugin/publishLocal
```

Run test

```
sbt > hello/compile; hello/run
```

Check the files under `hello`:

```
hello/
├── Hello.scala
├── instrument.yml
├── methods.csv
├── results.csv
```

## License

MIT License
