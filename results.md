# Results and interpretation

### Disclaimer

* All described benchmarks are highly synthetic and only touch a small subset of what could have been measured.
* Results may vary depending on the runtime environment (hardware, operating system and software, JDK version, etc).

### Caveats

Each benchmark has a warmup phase, providing the JVM optimization opportunities which may be unrealistic in real
world scenarios. In practice, some methods are just called a few times or even only once. Depending on the JVM options
(see `-Xcomp` and `-XX:CompileTreshold`), method compilation to native code only happens after a certain threshold
(e.g., 10000 invocations). Additionally, call sites must be linked on their first invocation. This first time
invocation can especially be costly for `invokedynamic` call sites, because linking happens in a bootstrap method
provided by the programmer.

To see these effects in action, run `./run_warmup_singleshot.sh`, which typically prints lines like these:

    # Run progress: 0,00% complete, ETA 00:00:00
    # Fork: 1 of 1
    # Warmup Iteration   1: 5067,868 us/op
    # Warmup Iteration   2: 132,388 us/op
    # Warmup Iteration   3: 80,232 us/op
    # Warmup Iteration   4: 78,538 us/op
    # Warmup Iteration   5: 74,499 us/op
    # Warmup Iteration   6: 73,966 us/op
    # Warmup Iteration   7: 77,861 us/op
    # Warmup Iteration   8: 76,433 us/op
    # Warmup Iteration   9: 72,412 us/op
    # Warmup Iteration  10: 78,548 us/op


TL;DR: don't forget to take initialization costs into account.

### Setup

Hardware:
- CPU: Intel(R) Core(TM) i5-2415M CPU @ 2.30GHz
- RAM: 8 GB DDR3 1333 MHz

Software:
- OS: Mac OS X 10.12.6 (Build 16G29)
- VM version: JDK 1.8.0_144, VM 25.144-b01 (if not explicitely specified otherwise)
- JMH version: 1.19


### Lookup APIs: MethodLookup

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

Explanation, speculation and remarks:

* `Class.getMethod()` has been introduced in JDK 1.1 (Feb '97) and probably was subject to heavy optimizations
  since then, whereas `java.lang.invoke` was introduced in JDK 1.7 (Jul '11).
* Down the call hierarchy, both APIs call a native method to get data from the VM. `Class` caches these results
  (see the source for `Class.privateGetDeclaredMethods()`), whereas `Lookup.findVirtual()` doesn't appear to
  implement such a cache.
* `Class.getMethod()` doesn't need to check whether the return type matches.
* Note that if a `SecurityManager` is active, `Class.getMethod()` performance will be affected negatively.


### Invoke APIs: StaticMethod and VirtualMethod

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


### Invoke instructions: RecursiveFibonacci

JDK 1.8.0_144, VM 25.144-b01:

    Benchmark                         (n)  Mode  Cnt   Score   Error  Units
    RecursiveFibonacci.invokedynamic   20  avgt   20  52,400 ± 0,980  us/op
    RecursiveFibonacci.invokestatic    20  avgt   20  42,223 ± 1,175  us/op

JDK 9, VM 9+181:

    Benchmark                         (n)  Mode  Cnt   Score   Error  Units
    RecursiveFibonacci.invokedynamic   20  avgt   20  42,021 ± 0,745  us/op
    RecursiveFibonacci.invokestatic    20  avgt   20  41,547 ± 0,727  us/op


Observations:

* JDK 1.8 VM: the `fib()` variant using `invokedynamic` is 24% slower than the `invokestatic` variant.
* JDK 9 VM: the `invokedynamic` variant is only 1% slower.


### Invoke instructions: RecursiveFibonacci warmup behavior

`RecursiveFibonacci.invokestatic`:

    # Warmup Iteration   1: 2125,666 us/op
    # Warmup Iteration   2: 109,460 us/op
    # Warmup Iteration   3: 62,530 us/op
    # Warmup Iteration   4: 69,093 us/op
    # Warmup Iteration   5: 66,939 us/op

`RecursiveFibonacci.invokedynamic`:

    # Warmup Iteration   1: 4393,227 us/op
    # Warmup Iteration   2: 124,310 us/op
    # Warmup Iteration   3: 74,280 us/op
    # Warmup Iteration   4: 72,018 us/op
    # Warmup Iteration   5: 76,052 us/op


Observations:

* Both variants get down to ~70 µs/call after the second `fib()` call.
* The first "cold" call to the `invokedynamic` variant of `fib()` takes more than twice as long as
  the `invokestatic` variant.
