#!/bin/sh
exec java -jar target/benchmarks.jar "$1" -w 5 -wi 5 -i 5 -f 1
