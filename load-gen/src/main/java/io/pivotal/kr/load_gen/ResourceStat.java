package io.pivotal.kr.load_gen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ResourceStat {
	public static List<Long> getPartitionPoints(String path, int partitionNumber) throws IOException {
		LineReader reader = new LineReader(204800, path);
		
		long fileSize = reader.getFileSize();
		
		long partitionSizeEach = (long)Math.floor((float)fileSize / partitionNumber);
		
		List<Long> getPartitionPoints = new ArrayList<Long>();
		getPartitionPoints.add(0L);
		
		int partitionCnt = 1;
		long pos = 0L;
		
		List<Line> lines = new ArrayList<Line>();
		
		while (reader.readLines(lines)) {
			for (int i=0; i<lines.size(); i++) {
				pos = lines.get(i).getOffset();
				
				if (pos > partitionSizeEach * partitionCnt) {
					getPartitionPoints.add(pos);
					partitionCnt++;
				}
			}
		}
		
		if (lines.size() > 0) {
			for (int i=0; i<lines.size(); i++) {
				pos = lines.get(i).getOffset();
				
				if (pos > partitionSizeEach * partitionCnt) {
					getPartitionPoints.add(pos);
					partitionCnt++;
				}
			}
		}
		
		getPartitionPoints.add(fileSize);
		reader.close();
		
		return getPartitionPoints;
	}
}
