package net.mmorgenstern.dynvoke_benchmark.indy;

import me.qmx.jitescript.CodeBlock;
import org.objectweb.asm.tree.LabelNode;
import org.openjdk.jmh.annotations.*;

import java.lang.invoke.MethodHandle;
import java.util.concurrent.TimeUnit;

import static java.lang.invoke.MethodType.methodType;

/**
 * Invoke instructions benchmark using a recursive Fibonacci function.
 *
 * Recursive fibonacci calls itself twice and does relatively cheap computations
 * otherwise, so it should be a good way to compare invokedynamic and invokestatic.
 *
 * Both versions are written using Jitescript, so we can make sure they are identical
 * otherwise.
 */
@State(Scope.Thread)
@Warmup(iterations = 20, time = 1)
@Measurement(iterations = 10, time = 1)
@Fork(2)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class RecursiveFibonacci {
    private final MethodHandle invokeStatic = initInvokeStatic();
    private final MethodHandle invokeDynamic = initInvokeDynamic();

    @Param({"20"})
    private int n;

    static MethodHandle initInvokeStatic() {
        final LabelNode labelNode = new LabelNode();

        DynamicFunction.Builder builder = new DynamicFunction.Builder();
        builder.setType(methodType(int.class, int.class));
        builder.setClassName("Fib");
        builder.setName("fib");
        builder.setCode(new CodeBlock() {{
            iload(0);
            iconst_1();
            if_icmple(labelNode);
            iload(0);
            iconst_1();
            isub();
            invokestatic(builder.getClassName(), builder.getName(), builder.getMethodDescriptor());
            iload(0);
            iconst_2();
            isub();
            invokestatic(builder.getClassName(), builder.getName(), builder.getMethodDescriptor());
            iadd();
            ireturn();
            label(labelNode);
            iconst_1();
            ireturn();
        }});
        return builder.build().handle();
    }

    static MethodHandle initInvokeDynamic() {
        final LabelNode labelNode = new LabelNode();

        DynamicFunction.Builder builder = new DynamicFunction.Builder();
        builder.setType(methodType(int.class, int.class));
        builder.setClassName("DynFib"); // höhöhö
        builder.setName("fib");
        builder.setCode(new CodeBlock() {{
            iload(0);
            iconst_1();
            if_icmple(labelNode);
            iload(0);
            iconst_1();
            isub();
            invokedynamic(builder.getName(), builder.getMethodDescriptor(), Bootstrap.bootstrapHandle("fib"));
            iload(0);
            iconst_2();
            isub();
            invokedynamic(builder.getName(), builder.getMethodDescriptor(), Bootstrap.bootstrapHandle("fib"));
            iadd();
            ireturn();
            label(labelNode);
            iconst_1();
            ireturn();
        }});
        return builder.build().handle();
    }

    @Benchmark
    public int invokestatic() throws Throwable {
        return (int) invokeStatic.invoke(n);
    }

    @Benchmark
    public int invokedynamic() throws Throwable {
        return (int) invokeDynamic.invoke(n);
    }
}
