Load-Gen
=========

Load-Gen is a load-generator that features parallel reads of data file(s).

Usage
----
java -jar target/load-gen-0.0.1-SNAPSHOT-jar-with-dependencies.jar localDir sqlMapConfigXMLPath delimiter numberOfThreads

a) sqlMapConfigXMLPath: contains JDBC-url, account-info, and reference to sql-mapping file

* sqlMapper.xml: where "insert" sql query is defined. modify as required, except for the xml-id, "insert".

b) delimiter: the delimiter that delimits each line in the data-file into columns as specified in the sql-mapping file.

c) localDir: directory that contains uniformly equal formatted data file(s).

Example Usage
----
a) from the project directory where pom.xml can be found,

b) run, mvn clean package

c) edit ./sqlMapConfig.xml for GemfireXD JDBC url as well as account-info. modify ./sqlMapper.xml as well according to your GemfireXD table-schema. 

d) run, java -jar target/load-gen-0.0.1-SNAPSHOT-jar-with-dependencies.jar "data" "./sqlMapConfig.xml" "," 500

* specifying more than 500-threads is not recommended, in which case, proper functioning of the program is not guaranteed.

What It Does
----
a) Reads and analyzes files in 'localDir' to partition read-points. The number of paritions will equal to 'numberOfThreads', and threads will be alloted to each file in proportion to their file-size.

b) For each partition will be created a thread for read and load-generation.

c) Uses GemfireXD JDBC sessions to load 'insert' requests to the designated GemfireXD Cluster.

Build
----
mvn clean package

Version
----
2.0

TODO
----
1. provide support for various JDBC drivers.

Credit
----
"Mong-ju Jung" <MongJu.Jung@emc.com> or, <mjung@pivotal.io> for Pivotal.
