#!/bin/sh

gfxd run -file=./PERF_SQL | grep -A2 "-" | grep -P "\d+"
