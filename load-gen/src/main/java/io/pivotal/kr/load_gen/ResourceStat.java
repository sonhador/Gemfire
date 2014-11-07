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
