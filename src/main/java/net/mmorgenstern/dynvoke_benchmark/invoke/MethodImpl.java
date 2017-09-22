package net.mmorgenstern.dynvoke_benchmark.invoke;

import org.openjdk.jmh.annotations.CompilerControl;

public class MethodImpl {
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public static String staticMethodNoInline() {
        return "staticMethod";
    }

    @CompilerControl(CompilerControl.Mode.INLINE)
    public static String staticMethod() {
        return "staticMethod";
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public Object virtualMethodNoInline() {
        return this;
    }

    @CompilerControl(CompilerControl.Mode.INLINE)
    public Object virtualMethod() {
        return this;
    }
}
