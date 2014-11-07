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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourceReader {
	private String dataDir;
	private int numOfThreads;
	
	private List<ResourceDescriptor> resourceDescriptors = new ArrayList<ResourceDescriptor>();
	
	public ResourceReader(String dataDir, int numOfThreads) throws IOException {
		this.dataDir = dataDir;
		this.numOfThreads = numOfThreads;
	
		getPartitionedResources();
	}
	
	public List<ResourceDescriptor> getResourceDescriptors() {
		return resourceDescriptors;
	}
	
	private void getPartitionedResources() throws IOException {
		List<String> filePaths = readFilePathsInDir();
		List<Long> fileSizes = readFileSizesInDir();
		
		long totalSize = 0L;
		for (long fileSize : fileSizes) {
			totalSize += fileSize;
		}
		
		List<Integer> filePartitions = new ArrayList<Integer>();
		
		for (long fileSize : fileSizes) {
			int partitions = (int)Math.floor((float)fileSize / totalSize * numOfThreads);
			if (partitions == 0) {
				partitions = 1;
			}
			filePartitions.add(partitions);
		}
		
		Map<Integer, List<Long>> filePartitionPoints = new HashMap<Integer, List<Long>>();
		
		List<Thread> waits = new ArrayList<Thread>();
		for (int i=0; i<filePaths.size(); i++) {
			String path = filePaths.get(i);
			
			Thread thread = new Thread(new PartitionPointAnalyzer(filePartitionPoints, i, path, filePartitions.get(i)));
			waits.add(thread);
		}
		
		for (Thread thread : waits) {
			thread.start();
		}
		
		for (Thread thread : waits) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		long recordCnt = 0;
		for (int i=0; i<filePaths.size(); i++) {
			String path = filePaths.get(i);
			int partitionPoints = filePartitionPoints.get(i).size();
			for (int j=0; j < partitionPoints - 1; j++) {
				
				long startPos = filePartitionPoints.get(i).get(j);
				long endPos = filePartitionPoints.get(i).get(j+1) - 1;
				
				resourceDescriptors.add(new ResourceDescriptor(path, startPos, endPos));
				
				recordCnt = recordCnt + (endPos - startPos + 1);
				
				System.out.println(startPos + " , " + endPos + " , " + (endPos - startPos + 1));
			}
			
			System.out.println("Partitions: " + partitionPoints);
			
			System.out.println("File, " + path + " analyzed for parallel read !!");
		}
	}
	
	private List<Long> readFileSizesInDir() throws IOException {
		File dir = new File(this.dataDir);;
		
		File[] files = dir.listFiles();
		
		List<Long> fileSizes = new ArrayList<Long>();
		
		for (File file : files) {
			fileSizes.add(file.length());
		}
		
		return fileSizes;
	}
	
	private List<String> readFilePathsInDir() throws IOException {
		File dir = new File(this.dataDir);;
		
		File[] files = dir.listFiles();
		 
		List<String> filePaths = new ArrayList<String>();
		
		for (File file : files) {
			filePaths.add(file.getCanonicalPath());
		}
		
		return filePaths;
	}
	
	private class PartitionPointAnalyzer implements Runnable {
		private Map<Integer, List<Long>> filePartitionPoints;
		private String path;
		private int partitions;
		private int fileSeq;
		
		public PartitionPointAnalyzer(Map<Integer, List<Long>> filePartitionPoints, int fileSeq, String path, int partitions) {
			this.filePartitionPoints = filePartitionPoints;
			this.fileSeq = fileSeq;
			this.path = path;
			this.partitions = partitions;
		}
		
		@Override
		public void run() {
			try {
				List<Long> partitionPoints = ResourceStat.getPartitionPoints(path, partitions);
				filePartitionPoints.put(fileSeq, partitionPoints);
				
				System.out.println(path + " analyzed for partitioned parallel read..");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
