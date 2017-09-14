package de.tu_dresden.indybench.callsites;

import de.tu_dresden.indybench.Methods;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;

import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;

import static java.lang.invoke.MethodType.methodType;
import static me.qmx.jitescript.util.CodegenUtils.p;
import static me.qmx.jitescript.util.CodegenUtils.sig;

/**
 * Contains bootstrap methods used in the benchmarks.
 */
public class Bootstrap {
    public static final String BOOTSTRAP_PATH = p(Bootstrap.class);
    public static final String BOOTSTRAP_SIG = sig(CallSite.class, Lookup.class, String.class, MethodType.class);

    public static Handle ASM_HANDLE = new Handle(Opcodes.H_INVOKESTATIC, BOOTSTRAP_PATH, "constCallSite", BOOTSTRAP_SIG);

    public static CallSite constCallSite(Lookup lookup, String name, MethodType type) throws Exception {
        return new ConstantCallSite(lookup.findStatic(Methods.class, "staticMethod", methodType(String.class)));
    }
}
