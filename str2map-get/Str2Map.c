/*******************************************************************************
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 MongJu Jung <mjung@pivotal.io>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/

#include "postgres.h"
#include "utils/builtins.h"
#include "fmgr.h"
#include <string.h>

#ifdef PG_MODULE_MAGIC
PG_MODULE_MAGIC;
#endif

#define NULL_CHAR '\0'

Datum get(PG_FUNCTION_ARGS);
PG_FUNCTION_INFO_V1(get);

void free_palloc(char *p);
void cleanup(char *body, char *key, char *key_val_pairs_delim, char *key_val_delim);
char *concat(char *dst, int dst_len, char *src, int src_len);

Datum get(PG_FUNCTION_ARGS) {
	char *body 			= text_to_cstring(PG_GETARG_TEXT_P(0));
 	char *key 			= text_to_cstring(PG_GETARG_TEXT_P(1));
  	char *key_val_pairs_delim 	= text_to_cstring(PG_GETARG_TEXT_P(2));
	char *key_val_delim 		= text_to_cstring(PG_GETARG_TEXT_P(3));
	char *key_part;
	char *val_part;
	char *val_part_end;
	int key_val_pairs_delim_len 	= strlen(key_val_pairs_delim);
	int key_len 			= strlen(key);
	int key_val_delim_len 		= strlen(key_val_delim);
	char key_delim[key_len + key_val_delim_len + 1];
	char delim_key_delim[key_val_pairs_delim_len + key_len + key_val_delim_len + 1];

	if (is_empty(body) || is_empty(key) || is_empty(key_val_pairs_delim) || is_empty(key_val_delim)) {
		cleanup(body, key, key_val_pairs_delim, key_val_delim);
		PG_RETURN_NULL();
	}

	concat(key_delim, 0, key, key_len);
	concat(key_delim, key_len, key_val_delim, key_val_delim_len);

	concat(delim_key_delim, 0, key_val_pairs_delim, key_val_pairs_delim_len);
	concat(delim_key_delim, key_val_pairs_delim_len, key_delim, key_len + key_val_delim_len);

	key_part = strstr(body, key_delim);

	if (key_part != body) {
		key_part = strstr(body, delim_key_delim);

		if (!key_part) {
			cleanup(body, key, key_val_pairs_delim, key_val_delim);
			PG_RETURN_NULL();
		}

		key_part += key_val_pairs_delim_len;
	}

	val_part = key_part + key_len + key_val_delim_len;
	val_part_end = strstr(val_part, key_val_pairs_delim);

	text *val;
	if (val_part_end) {
		if (val_part_end == val_part) {
			cleanup(body, key, key_val_pairs_delim, key_val_delim);
			PG_RETURN_NULL();
		}

		val = cstring_to_text_with_len(val_part, val_part_end - val_part);
	} else {
		if (val_part == NULL_CHAR) {
			cleanup(body, key, key_val_pairs_delim, key_val_delim);
			PG_RETURN_NULL();
		}

		val = cstring_to_text(val_part);
	}

	cleanup(body, key, key_val_pairs_delim, key_val_delim);
	PG_RETURN_TEXT_P(val);
}

void free_palloc(char *p) {
	if (p == NULL) {
		return;
	}

	pfree(p);
}

void cleanup(char *body, char *key, char *key_val_pairs_delim, char *key_val_delim) {
	free_palloc(body);
	free_palloc(key);
	free_palloc(key_val_pairs_delim);
	free_palloc(key_val_delim);
}

char *concat(char *dst, int dst_len, char *src, int src_len) {
	memcpy(dst + dst_len, src, src_len);
	dst[dst_len + src_len] = NULL_CHAR;

	return dst;
}

int is_empty(char *p) {
	if (p == NULL || p == NULL_CHAR) {
		return 1;
	}

	return 0;
}
