package de.tu_dresden.indybench;

import org.openjdk.jmh.annotations.CompilerControl;

/**
 * Methods to be invoked by the different APIs.
 */
public class Methods {
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public static String staticMethod() {
        return "staticMethod";
    }
}
