package de.tu_dresden.indybench.invoke;

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
}
