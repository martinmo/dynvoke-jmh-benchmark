package de.tu_dresden.indybench.indy;

import me.qmx.jitescript.CodeBlock;
import org.openjdk.jmh.annotations.*;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static de.tu_dresden.indybench.indy.Bootstrap.bootstrapHandle;
import static de.tu_dresden.indybench.indy.CodeLoader.createHandle;
import static java.lang.invoke.MethodType.methodType;
import static me.qmx.jitescript.util.CodegenUtils.p;

@Warmup(iterations = 10, time = 1)
@Measurement(iterations = 10, time = 1)
@Fork(2)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class SimpleThroughput {
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public static int add(int x, int y) {
        // something really cheap
        return x + y;
    }

    @State(Scope.Thread)
    public static class Handles {
        MethodHandle mh;
        MethodHandle indy;
        MethodHandle java;

        @Setup(Level.Trial)
        public void init() throws Throwable {
            final MethodType type = methodType(int.class, int.class, int.class);
            mh = MethodHandles.lookup().findStatic(SimpleThroughput.class, "add", type);
            java = createHandle(new CodeBlock() {{
                iload(0);
                iload(1);
                invokestatic(p(SimpleThroughput.class), "add", type.toMethodDescriptorString());
                ireturn();
            }}, type);
            indy = createHandle(new CodeBlock() {{
                iload(0);
                iload(1);
                invokedynamic("add", type.toMethodDescriptorString(), bootstrapHandle("staticMethod"));
                ireturn();
            }}, type);
        }
    }

    @State(Scope.Thread)
    public static class Values {
        int x;
        int y;

        @Setup(Level.Iteration)
        public void init() {
            Random random = new Random();
            x = random.nextInt();
            y = random.nextInt();
        }
    }

    @Benchmark
    public void baseline() {
        // intentionally left blank
    }

    @Benchmark
    public int baseline_java(Values values, Handles handles) throws Throwable {
        return add(values.x, values.y);
    }

    @Benchmark
    public int baseline_invoke(Values values, Handles handles) throws Throwable {
        return (int) handles.mh.invoke(values.x, values.y);
    }

    @Benchmark
    public int invokedynamic(Values values, Handles handles) throws Throwable {
        return (int) handles.indy.invoke(values.x, values.y);
    }

    @Benchmark
    public int invokestatic(Values values, Handles handles) throws Throwable {
        return (int) handles.java.invoke(values.x, values.y);
    }
}
