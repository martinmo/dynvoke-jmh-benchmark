package de.tu_dresden.indybench.indy;

import org.openjdk.jmh.annotations.Warmup;

// try higher values to see how profiling impacts runtime
@Warmup(iterations = 1)
public class SimpleSingleShotWithWarmup extends SimpleSingleShot {}
