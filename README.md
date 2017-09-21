# Invokedynamic Benchmarks

This is a work-in-progress set of benchmarks to compare the runtime overhead of
different dynamic invocation and lookup primitives on the JVM, such as:

* `Class.getMethod()`
* `Lookup.findStatic()` and `Lookup.findVirtual()`(`java.lang.invoke`)
* `Method.invoke()` (`java.lang.reflect`)
* `MethodHandle.invoke()` (`java.lang.invoke`)
* `invokedynamic`

This benchmark wouldn't be possible without two great tools:
* The [Java Microbenchmarking Harness][jmh] is used in order to get reliable results
  and to avoid some common benchmarking pitfalls on the HotSpot JVM.
* [JiteScript][jite] is used to dynamically generate code leveraging the `invokedynamic`
  instruction (which would otherwise not be possible with pure Java).

Other sources of inspiration include:
* https://github.com/golo-lang/golo-jmh-benchmarks
* https://github.com/shipilev/article-method-dispatch

## Results

See the [results of benchmark runs on my machine](results.md).

## Build

    mvn clean package

## Run

    ./run_benchmarks.sh ...

or

    java -jar target/benchmarks.jar ...


[jmh]: http://openjdk.java.net/projects/code-tools/jmh/
[jite]: https://github.com/qmx/jitescript
