Load-Gen
=========

Load-Gen is a load-generator that features parallel reads of data file(s).

Usage
----
java -jar ./target/load-gen-0.0.1-SNAPSHOT-jar-with-dependencies.jar localDir destIp destPort secondsLaterToStart secondsToRun numberOfThreads

Example Usage
----
java -Xmx2048m -jar ./target/load-gen-0.0.1-SNAPSHOT-jar-with-dependencies.jar ./data localhost 80 10 300 300

What It Does
----
1. Reads and analyzes files in \<localDir\> to partition read-points. The number of paritions will equal to \<numberOfThreads\>, and threads will be alloted to each file in proportion to their file-size.
2. For each partition will be created a thread for read and load-generation.
3. Currently, a POST message for http://\<destIp\>:\<destPort\>/?data=\<file-read-data\> will be generated and called.

Build
----
mvn clean package

Version
----
1.0

TODO
----
1. provide header/data templates for protocols.

Credit
----
"Mong-ju Jung" <MongJu.Jung@emc.com> for Pivotal.
