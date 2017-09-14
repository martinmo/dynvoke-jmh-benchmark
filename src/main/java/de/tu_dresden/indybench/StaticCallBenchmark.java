package de.tu_dresden.indybench;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

import static java.lang.invoke.MethodType.methodType;

@State(Scope.Benchmark)
public class StaticCallBenchmark {
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.publicLookup();

    private final Method method;
    private final MethodHandle handle;

    public StaticCallBenchmark() {
        try {
            method = getClass().getMethod("staticMethod");
            handle = LOOKUP.findStatic(getClass(), "staticMethod", methodType(String.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // The method to be called
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public static String staticMethod() {
        return "staticMethod";
    }

    @Benchmark
    public void baseline() throws Throwable {
    }

    @Benchmark
    public Object directCall() throws Throwable {
        return staticMethod();
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
}
