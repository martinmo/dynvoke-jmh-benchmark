package de.tu_dresden.indybench.callsites;

import me.qmx.jitescript.CodeBlock;
import me.qmx.jitescript.JDKVersion;
import me.qmx.jitescript.JiteClass;

import java.util.function.Supplier;

import static me.qmx.jitescript.util.CodegenUtils.c;
import static me.qmx.jitescript.util.CodegenUtils.p;
import static me.qmx.jitescript.util.CodegenUtils.sig;

/**
 * Factory for easy creation of Supplier objects using the Jitescript DSL.
 *
 * Why Supplier? Because the return value can be used in JMH to prevent dead
 * code optimizations by the JVM JIT compiler, which could ultimately lead to
 * measuring the wrong thing (i.e., noop instead of a method call).
 */
public class Suppliers {
    private static final DynamicClassLoader LOADER = new DynamicClassLoader();

    static class DynamicClassLoader extends ClassLoader {
        public Class<?> define(JiteClass jiteClass) {
            byte[] classBytes = jiteClass.toBytes(JDKVersion.V1_8);
            return super.defineClass(c(jiteClass.getClassName()), classBytes, 0, classBytes.length);
        }
    }

    public static <T> Supplier<T> newSupplier(String name, CodeBlock block) {
        JiteClass jiteClass = new JiteClass(name, p(Object.class), new String[]{ p(Supplier.class) }) {
            {
                defineDefaultConstructor();
                defineMethod("get", ACC_PUBLIC, sig(Object.class), block); // not T because of type erasure
            }
        };
        try {
            Class<?> cls = LOADER.define(jiteClass);
            return (Supplier<T>) cls.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
