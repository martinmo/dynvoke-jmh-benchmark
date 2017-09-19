package de.tu_dresden.indybench.invoke;

import org.openjdk.jmh.annotations.*;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import static java.lang.invoke.MethodType.methodType;

@Warmup(iterations = 10, time = 1)
@Measurement(iterations = 10, time = 1)
@Fork(2)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class VirtualMethod {
    @Param({"true", "false"})
    private boolean inline;

    private MethodImpl methodImpl;
    private MethodHandle handle;
    private Method method;

    @Setup
    public void setup() throws Exception {
        String name = inline ? "virtualMethod" : "virtualMethodNoInline";
        handle = MethodHandles.lookup().findVirtual(MethodImpl.class, name, methodType(Object.class));
        method = MethodImpl.class.getMethod(name);
        methodImpl = new MethodImpl();
    }

    /**
     * The bytecode for this method always contains "iconst_0; anewarray",
     * which is due to the varargs list in Method.invoke(obj, args...).
     */
    @Benchmark
    public Object Method_invoke() throws Throwable {
        return method.invoke(methodImpl);
    }

    @Benchmark
    public Object MethodHandle_invoke() throws Throwable {
        return (Object) handle.invoke(methodImpl);
    }
}
