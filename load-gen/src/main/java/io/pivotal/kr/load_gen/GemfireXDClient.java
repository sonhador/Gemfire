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
package io.pivotal.kr.load_gen;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;


public class GemfireXDClient {
	private SqlSessionManager sqlSessionManager;
	
	public GemfireXDClient (String sqlMapConfigXMLPath) throws FileNotFoundException {
		sqlSessionManager = SqlSessionManager.newInstance(new FileReader(new File(sqlMapConfigXMLPath)));
	}

	public SqlSession getSession() throws FileNotFoundException {
		return sqlSessionManager.openSession(true);
	}
	
	public void closeSession(SqlSession sqlSession) {
		sqlSession.close();
	}
	
	private Map<String, String> delimitedStringToMap(String line, String delimiter) {
		if (line.contains(delimiter) == false) {
			return null;
		}
		
		Map<String, String> map = new HashMap<String, String>();
		
		String []elems = line.split(delimiter);
		
		for (int i=1; i<=elems.length; i++) {
			map.put("idx" + i, elems[i-1].trim());
		}
		
		return map;
	}
	
	public boolean crud(String mode, SqlSession sqlSession, String []lines, String delimiter) {
		if (lines == null || lines.length == 0) {
			return false;
		}
		
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		
		for (String line : lines) {
			list.add(delimitedStringToMap(line, delimiter));			
		}
		
		if (list.size() == 0) {
			return false;
		}
		
		if ("insert".equals(mode)) {
			for (Map<String, String> param : list) {
				sqlSession.insert("insert", param);
			}
		} else if ("select".equals(mode)) {
			for (Map<String, String> param : list) {
				sqlSession.selectOne("select", param);
			}
		} else if ("update".equals(mode)) {
			for (Map<String, String> param : list) {
				sqlSession.update("update", param);
			}
		} else if ("delete".equals(mode)) {
			for (Map<String, String> param : list) {
				sqlSession.delete("delete", param);
			}
		}
		
		return true;
	}
}
