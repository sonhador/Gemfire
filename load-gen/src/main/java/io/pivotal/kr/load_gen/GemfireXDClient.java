package io.pivotal.kr.load_gen;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;


public class GemfireXDClient {
	private SqlSessionManager sqlSessionManager;
	
	public GemfireXDClient (String sqlMapConfigXMLPath) throws FileNotFoundException {
		sqlSessionManager = SqlSessionManager.newInstance(new FileReader(new File(sqlMapConfigXMLPath)));
	}

	public SqlSession getSession() throws FileNotFoundException {
		return sqlSessionManager.openSession(ExecutorType.BATCH, false);
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
			sqlSession.insert("insert", list);
			sqlSession.commit();
		} else if ("select".equals(mode)) {
			for (Map<String, String> param : list) {
				sqlSession.selectOne("select", param);
			}
			sqlSession.flushStatements();
		} else if ("update".equals(mode)) {
			for (Map<String, String> param : list) {
				sqlSession.update("update", param);
			}
			sqlSession.commit();
		} else if ("delete".equals(mode)) {
			for (Map<String, String> param : list) {
				sqlSession.delete("delete", param);
			}
			sqlSession.commit();
		}
		
		return true;
	}
}
