package net.mmorgenstern.dynvoke_benchmark.indy;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;

import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;

import static me.qmx.jitescript.util.CodegenUtils.p;
import static me.qmx.jitescript.util.CodegenUtils.sig;

public class Bootstrap {
    public static final String BOOTSTRAP_PATH = p(Bootstrap.class);
    public static final String BOOTSTRAP_SIG = sig(CallSite.class, Lookup.class, String.class, MethodType.class);

    public static CallSite fib(Lookup lookup, String name, MethodType type) throws Exception {
        return new ConstantCallSite(lookup.findStatic(lookup.lookupClass(), name, type));
    }

    public static Handle bootstrapHandle(String name) {
        return new Handle(Opcodes.H_INVOKESTATIC, BOOTSTRAP_PATH, name, BOOTSTRAP_SIG);
    }
}
