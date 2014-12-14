Str2Map - PL/C Procedure
=========

Str2Map is a PostgreSQL PL/C Procedure that lets you parse and get values for map-styled string expression.

Example Usage
----
CREATE OR REPLACE FUNCTION get(body text, key text, keyValPairsDelim text, keyValDelim text)
RETURNS TEXT
  AS 'Str2Map.so', 'get'
LANGUAGE c
STRICT
IMMUTABLE;

DROP TABLE IF EXISTS test_t;
CREATE TABLE test_t (
body text
);
INSERT INTO test_t VALUES
('k1:v1,k2:,k3:'),
('k1:,k2:v2,k3:'),
('k1:va1,k2:val2,k3:val3'),
('kkk:k1,kk:k2,kkkk:k3');

SELECT get(body, 'k1', ',', ':') as k1,
       get(body, 'k2', ',', ':') as k2,
       get(body, 'k3', ',', ':') as k3,
       get(body, 'k4', ',', ':') as k4,
       get(body, 'kk', ',', ':') as kk
FROM test_t;

Build & Deploy & Register
----
a) build and deploy using build_deploy.sh
b) register as in register_plc.sql

Version
----
1.0

Credit
----
"MongJu Jung" <mjung@pivotal.io>
