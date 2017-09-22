package net.mmorgenstern.dynvoke_benchmark.lookup;

import org.openjdk.jmh.annotations.*;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.concurrent.TimeUnit;

import static java.lang.invoke.MethodType.methodType;

@Warmup(iterations = 10, time = 1)
@Measurement(iterations = 10, time = 1)
@Fork(2)
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class MethodLookup {
    private static final MethodType TYPE = methodType(String.class);

    private MethodHandles.Lookup lookup = MethodHandles.lookup();
    private MethodHandles.Lookup publicLookup = MethodHandles.publicLookup();
    private Class<?> clazz = Child.class;

    @Param({"parentOnly", "childOnly"})
    private String methodName;

    @Benchmark
    public Object Class_getMethod() throws Throwable {
        return clazz.getMethod(methodName);
    }

    @Benchmark
    public Object lookup_unreflect_Class_getMethod() throws Throwable {
        return lookup.unreflect(clazz.getMethod(methodName));
    }

    @Benchmark
    public Object lookup_findVirtual() throws Throwable {
        return lookup.findVirtual(clazz, methodName, TYPE);
    }

    @Benchmark
    public Object publicLookup_findVirtual() throws Throwable {
        return publicLookup.findVirtual(clazz, methodName, TYPE);
    }
}
