package net.mmorgenstern.dynvoke_benchmark.indy;

import org.junit.Test;

import java.lang.invoke.MethodHandle;

import static org.junit.Assert.assertEquals;

public class RecursiveFibTest {
    @Test
    public void testInvokeStatic() throws Throwable {
        assertFibSequence(RecursiveFibonacci.initInvokeStatic());
    }

    @Test
    public void testInvokeDynamic() throws Throwable {
        assertFibSequence(RecursiveFibonacci.initInvokeDynamic());
    }

    public void assertFibSequence(MethodHandle mh) throws Throwable {
        assertEquals(1, mh.invoke(0));
        assertEquals(1, mh.invoke(1));
        assertEquals(2, mh.invoke(2));
        assertEquals(3, mh.invoke(3));
        assertEquals(5, mh.invoke(4));
        assertEquals(8, mh.invoke(5));
        assertEquals(89, mh.invoke(10));
    }
}
