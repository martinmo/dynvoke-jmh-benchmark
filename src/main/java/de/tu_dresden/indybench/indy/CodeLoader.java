package de.tu_dresden.indybench.indy;

import me.qmx.jitescript.CodeBlock;
import me.qmx.jitescript.JDKVersion;
import me.qmx.jitescript.JiteClass;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import static me.qmx.jitescript.util.CodegenUtils.c;


public class CodeLoader {
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    private static final String METHOD_NAME = "compute";

    static class DynamicClassLoader extends ClassLoader {
        public Class<?> define(JiteClass jiteClass) {
            byte[] classBytes = jiteClass.toBytes(JDKVersion.V1_8);
            return super.defineClass(c(jiteClass.getClassName()), classBytes, 0, classBytes.length);
        }
    }

    public static MethodHandle createHandle(CodeBlock block, MethodType type) throws Throwable {
        JiteClass jiteClass = new JiteClass("Dynamic") {{
            defineDefaultConstructor();
            defineMethod(METHOD_NAME, ACC_PUBLIC | ACC_STATIC, type.toMethodDescriptorString(), block);
        }};
        return LOOKUP.findStatic(new DynamicClassLoader().define(jiteClass), METHOD_NAME, type);
    }
}
