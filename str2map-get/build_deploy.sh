#!/bin/sh

rm -f *.o *.so

FLAGS="-m64 -O3 -ffast-math -march=native -funroll-loops"

HAWQ=/usr/lib/gphd/hawq
LIB=$HAWQ/lib
INC=$HAWQ/include
INC_POSTGRESQL=$INC/postgresql
INC_SERVER=$INC_POSTGRESQL/server
INC_INTERNAL=$INC_POSTGRESQL/internal

gcc $FLAGS -I$INC -I$INC_SERVER -I$INC_INTERNAL -L$LIB -fPIC -c Str2Map.c

gcc -shared -o Str2Map.so Str2Map.o

sudo cp Str2Map.so /usr/lib/gphd/hawq/lib/postgresql/.
