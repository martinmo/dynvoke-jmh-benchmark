package de.tu_dresden.indybench.callsites;

import de.tu_dresden.indybench.Methods;
import me.qmx.jitescript.CodeBlock;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.util.function.Supplier;

import static de.tu_dresden.indybench.callsites.Suppliers.newSupplier;
import static me.qmx.jitescript.util.CodegenUtils.p;
import static me.qmx.jitescript.util.CodegenUtils.sig;

@State(Scope.Benchmark)
public class ConstCallSiteBenchmark {
    private final Supplier<String> indyCall = newSupplier("Invokedynamic", new CodeBlock() {
        {
            invokedynamic("staticMethod", sig(String.class), Bootstrap.ASM_HANDLE);
            areturn();
        }
    });

    private final Supplier<String> staticCall = newSupplier("Invokestatic", new CodeBlock() {
        {
            invokestatic(p(Methods.class), "staticMethod", sig(String.class));
            areturn();
        }
    });

    @Benchmark
    public String invokedynamic() {
        return indyCall.get();
    }

    @Benchmark
    public String baseline() {
        return staticCall.get();
    }
}
