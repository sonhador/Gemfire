CREATE OR REPLACE FUNCTION get(body text, key text, keyValPairsDelim text, keyValDelim text)
RETURNS TEXT
  AS 'Str2Map.so', 'get'
LANGUAGE c
STRICT
IMMUTABLE;
