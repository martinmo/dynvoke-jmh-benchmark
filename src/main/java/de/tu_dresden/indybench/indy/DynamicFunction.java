package de.tu_dresden.indybench.indy;

import me.qmx.jitescript.CodeBlock;
import me.qmx.jitescript.JDKVersion;
import me.qmx.jitescript.JiteClass;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import static me.qmx.jitescript.util.CodegenUtils.c;

/**
 * Overengineered dynamic function generator to make the benchmark code look pretty.
 */
public class DynamicFunction {
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    public static class Builder {
        private MethodType type;

        public MethodType getType() {
            return type;
        }

        public void setType(MethodType type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public CodeBlock getCode() {
            return code;
        }

        public void setCode(CodeBlock code) {
            this.code = code;
        }

        public String getMethodDescriptor() {
            return type.toMethodDescriptorString();
        }

        private String name, className;
        private CodeBlock code;

        public DynamicFunction build() {
            return new DynamicFunction(type, name, className, code);
        }
    }

    static class DynamicClassLoader extends ClassLoader {
        public Class<?> define(JiteClass jiteClass) {
            byte[] classBytes = jiteClass.toBytes(JDKVersion.V1_8);
            return super.defineClass(c(jiteClass.getClassName()), classBytes, 0, classBytes.length);
        }
    }

    private final MethodHandle handle;

    private DynamicFunction(MethodType type, String name, String className, CodeBlock block) {
        JiteClass jiteClass = new JiteClass(className) {{
            defineDefaultConstructor();
            defineMethod(name, ACC_PUBLIC | ACC_STATIC, type.toMethodDescriptorString(), block);
        }};
        Class<?> clazz = new DynamicClassLoader().define(jiteClass);
        try {
            handle = LOOKUP.findStatic(clazz, name, type);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new AssertionError(e);
        }
    }

    public MethodHandle handle() {
        return handle;
    }
}