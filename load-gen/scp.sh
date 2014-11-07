#!/bin/sh

PWD="changeme"

expect <<EOD
spawn scp target/load-gen-0.0.1-SNAPSHOT-jar-with-dependencies.jar root@10.35.106.69:/root/load-gen/.
expect "password"
send "$PWD\n"

sleep 1

spawn scp target/load-gen-0.0.1-SNAPSHOT-jar-with-dependencies.jar root@10.35.106.166:/root/load-gen/.
expect "password"
send "$PWD\n"

sleep 1

spawn scp target/load-gen-0.0.1-SNAPSHOT-jar-with-dependencies.jar root@10.35.106.167:/root/load-gen/.
expect "password"
send "$PWD\n"

sleep 1

spawn scp sqlMapper.xml root@10.35.106.69:/root/load-gen/.
expect "password"
send "$PWD\n"

sleep 1

spawn scp sqlMapper.xml root@10.35.106.166:/root/load-gen/.
expect "password"
send "$PWD\n"

sleep 1

spawn scp sqlMapper.xml root@10.35.106.167:/root/load-gen/.
expect "password"
send "$PWD\n"
EOD
