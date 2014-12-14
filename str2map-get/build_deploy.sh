#!/bin/sh

rm -f *.o *.so

gcc -I/usr/lib/gphd/hawq/include/postgresql/server/ -L/usr/lib/gphd/hawq/lib/ -fPIC -c Str2Map.c

gcc -shared -o Str2Map.so Str2Map.o

sudo cp Str2Map.so /usr/lib/gphd/hawq/lib/postgresql/.
