package de.tu_dresden.indybench.indy;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@Warmup(iterations = 0)
@Measurement(iterations = 1)
@Fork(5)
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class SimpleSingleShot extends SimpleThroughput {}
