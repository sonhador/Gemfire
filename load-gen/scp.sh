#*******************************************************************************
# The MIT License (MIT)
#
# Copyright (c) 2014 MongJu Jung <mjung@pivotal.io>
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in
# all copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
# THE SOFTWARE.
#*******************************************************************************
#!/bin/sh

PWD="changeme"

expect <<EOD
spawn scp target/load-gen-0.0.1-SNAPSHOT-jar-with-dependencies.jar root@10.35.106.69:/root/load-gen/.
expect "password"
send "$PWD\n"
expect eof

spawn scp target/load-gen-0.0.1-SNAPSHOT-jar-with-dependencies.jar root@10.35.106.166:/root/load-gen/.
expect "password"
send "$PWD\n"
expect eof

spawn scp target/load-gen-0.0.1-SNAPSHOT-jar-with-dependencies.jar root@10.35.106.167:/root/load-gen/.
expect "password"
send "$PWD\n"
expect eof

spawn scp sqlMapper.xml root@10.35.106.69:/root/load-gen/.
expect "password"
send "$PWD\n"
expect eof

spawn scp sqlMapper.xml root@10.35.106.166:/root/load-gen/.
expect "password"
send "$PWD\n"
expect eof

spawn scp sqlMapper.xml root@10.35.106.167:/root/load-gen/.
expect "password"
send "$PWD\n"
expect eof
EOD
