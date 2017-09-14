# Invokedynamic Benchmarks

This is a work-in-progress set of benchmarks to compare the runtimes of different dynamic
invocation and lookup APIs in Java, such as:

* `java.lang.reflect.Method`
* `java.lang.invoke.MethodHandle`
* `java.lang.reflect.Proxy`
* `invokedynamic`

All benchmarks use the [Java Microbenchmarking Harness][jmh].


## Build

    mvn clean package

## Run

    ./run_benchmarks.sh

or

    java -jar target/benchmarks.jar ...


[jmh]: http://openjdk.java.net/projects/code-tools/jmh/
