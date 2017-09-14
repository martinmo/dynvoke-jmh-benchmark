package de.tu_dresden.indybench;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import static java.lang.invoke.MethodType.methodType;

@State(Scope.Benchmark)
public class LookupBenchmark {
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.publicLookup();
    private static final MethodType TYPE = methodType(String.class);
    private final Class<?> clazz = getClass();

    @Benchmark
    public Object reflectLookup() throws Throwable {
        return clazz.getMethod("toString");
    }

    @Benchmark
    public Object indyVirtualLookup() throws Throwable {
        return LOOKUP.findVirtual(clazz, "toString", TYPE);
    }
}
