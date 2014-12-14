Str2Map - PL/C Procedure
=========

Str2Map is a PostgreSQL PL/C Procedure that lets you parse and get values for map-styled string expression.

Example Usage
----
CREATE OR REPLACE FUNCTION get(body text, key text, keyValPairsDelim text, keyValDelim text)<br/>
RETURNS TEXT<br />
  AS 'Str2Map.so', 'get'<br/>
LANGUAGE c<br/>
STRICT<br/>
IMMUTABLE;

DROP TABLE IF EXISTS test_t;<br/>
CREATE TABLE test_t (<br/>
body text<br/>
);<br/>
INSERT INTO test_t VALUES<br/>
('k1:v1,k2:,k3:'),<br/>
('k1:,k2:v2,k3:'),<br/>
('k1:va1,k2:val2,k3:val3'),<br/>
('kkk:k1,kk:k2,kkkk:k3');<br/>

SELECT get(body, 'k1', ',', ':') as k1,<br/>
       get(body, 'k2', ',', ':') as k2,<br/>
       get(body, 'k3', ',', ':') as k3,<br/>
       get(body, 'k4', ',', ':') as k4,<br/>
       get(body, 'kk', ',', ':') as kk<br/>
FROM test_t;

Build & Deploy & Register
----
a) build and deploy using build_deploy.sh<br/>
b) register as in register_plc.sql

Version
----
1.0

Credit
----
"MongJu Jung" <mjung@pivotal.io> 
