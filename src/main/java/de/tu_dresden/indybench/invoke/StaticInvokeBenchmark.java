package de.tu_dresden.indybench.invoke;

import de.tu_dresden.indybench.Methods;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

import static java.lang.invoke.MethodType.methodType;

@State(Scope.Benchmark)
public class StaticInvokeBenchmark {
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.publicLookup();

    private final Method method;
    private final MethodHandle handle;
    private final MethodHandle unreflectHandle;

    public StaticInvokeBenchmark() {
        try {
            method = Methods.class.getMethod("staticMethod");
            handle = LOOKUP.findStatic(Methods.class, "staticMethod", methodType(String.class));
            unreflectHandle = LOOKUP.unreflect(method);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    public void baseline() throws Throwable {
    }

    @Benchmark
    public Object directCall() throws Throwable {
        return Methods.staticMethod();
    }

    /**
     * The bytecode for this method always contains "iconst_0; anewarray",
     * which is due to the varargs list in Method.invoke(obj, args...).
     */
    @Benchmark
    public Object reflectCall() throws Throwable {
        return method.invoke(null);
    }

    @Benchmark
    public Object invokeCall() throws Throwable {
        return (String) handle.invoke();
    }

    @Benchmark
    public Object unreflectInvokeCall() throws Throwable {
        return (String) unreflectHandle.invoke();
    }
}
