#!/bin/sh

# To see the effect of the optimizing JIT compilation, run this script
# and look at the decreasing execution times during the warmup iteratons.

exec java -jar target/benchmarks.jar -bm ss -wi 30 -i 1 -f 1 -tu us $@
