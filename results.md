# Results and interpretation

### Disclaimer

* All described benchmarks are highly synthetic and only touch a small subset of what could have been measured.
* Results may vary depending on the runtime environment (hardware, operating system and software, JDK version, etc).


### Setup

Hardware:
- CPU: Intel(R) Core(TM) i5-2415M CPU @ 2.30GHz
- RAM: 8 GB DDR3 1333 MHz

Software:
- OS: Mac OS X 10.12.6 (Build 16G29)
- VM version: JDK 1.8.0_144, VM 25.144-b01
- JMH version: 1.19


### Lookup APIs

    Benchmark                                      (methodName)  Mode  Cnt  Score   Error  Units
    MethodLookup.Class_getMethod                     parentOnly  avgt   20  0,394 ± 0,005  us/op
    MethodLookup.Class_getMethod                      childOnly  avgt   20  0,238 ± 0,023  us/op
    MethodLookup.lookup_findVirtual                  parentOnly  avgt   20  1,692 ± 0,153  us/op
    MethodLookup.lookup_findVirtual                   childOnly  avgt   20  1,643 ± 0,105  us/op
    MethodLookup.lookup_unreflect_Class_getMethod    parentOnly  avgt   20  0,985 ± 0,052  us/op
    MethodLookup.lookup_unreflect_Class_getMethod     childOnly  avgt   20  0,804 ± 0,047  us/op
    MethodLookup.publicLookup_findVirtual            parentOnly  avgt   20  2,123 ± 0,143  us/op
    MethodLookup.publicLookup_findVirtual             childOnly  avgt   20  2,168 ± 0,127  us/op


Observations:

* The Reflection API `Class.getMethod()` call is up to 9x faster than the new Lookup API in `java.lang.invoke`.
* The `Lookup` object provided by `MethodHandles.publicLookup()` is a little bit slower (0.4 - 0.5 µs) than one
  that is created by `MethodHandles.lookup()`.
* Even the combination of `Class.getMethod()` plus `Lookup.unreflect()` is up to twice as fast than the equivalent
  pure `Lookup.findVirtual()` call.
* At least for the reflective API, it seems to matter how "far away" an inherited method was implemented.


### Invoke APIs

    Benchmark                          (inline)  Mode  Cnt   Score   Error  Units
    StaticMethod.MethodHandle_invoke       true  avgt   20   7,716 ± 0,344  ns/op
    StaticMethod.MethodHandle_invoke      false  avgt   20   9,677 ± 0,131  ns/op
    StaticMethod.Method_invoke             true  avgt   20   9,114 ± 0,144  ns/op
    StaticMethod.Method_invoke            false  avgt   20  10,057 ± 0,115  ns/op
    VirtualMethod.MethodHandle_invoke      true  avgt   20   8,221 ± 0,146  ns/op
    VirtualMethod.MethodHandle_invoke     false  avgt   20  10,899 ± 0,271  ns/op
    VirtualMethod.Method_invoke            true  avgt   20   9,216 ± 0,129  ns/op
    VirtualMethod.Method_invoke           false  avgt   20  10,568 ± 0,163  ns/op


Observations:

* If the target method isn't inlined, both `invoke()` methods are identical in speed.
* Otherwise, `MethodHandle.invoke()` is 11% to 15% faster than `Method.invoke()`.
* Virtual method invocation is generally slower than static method invocation.


### Invoke instructions

    Benchmark                         (n)  Mode  Cnt   Score   Error  Units
    RecursiveFibonacci.invokedynamic   20  avgt   20  52,400 ± 0,980  us/op
    RecursiveFibonacci.invokestatic    20  avgt   20  42,223 ± 1,175  us/op


Observations:

* The `fib()` version using `invokedynamic` is 24% slower than the `invokestatic` version.
