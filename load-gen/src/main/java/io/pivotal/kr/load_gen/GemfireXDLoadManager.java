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
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

public class GemfireXDLoadManager {
	private List<GemfireXDLoadWorker> workers = new ArrayList<GemfireXDLoadWorker>();
	
	private AtomicInteger threadCnt;
	
	public GemfireXDLoadManager(String mode, String dataDir, String sqlMapConfigXMLPath, String delimiter, int numOfThreads) throws IOException {
		ResourceReader resourceReader = new ResourceReader(dataDir, numOfThreads);
		
		threadCnt = new AtomicInteger(0);
		
		int threadId = 1;
		for (ResourceDescriptor resource : resourceReader.getResourceDescriptors()) {
			GemfireXDLoadWorker thread = new GemfireXDLoadWorker(this, threadId++, resource, delimiter, new GemfireXDClient(sqlMapConfigXMLPath), mode); 
			workers.add(thread);
			
			countUpWorkToDo();
			
			new Thread(thread).start();
		}
		
		new Thread(new StatPrinter()).start();
	}
	
	public void countDownWorkDone() {
		threadCnt.decrementAndGet();
	}
	
	public void countUpWorkToDo() {
		threadCnt.incrementAndGet();
	}
	
	private class StatPrinter extends TimerTask {
		private boolean finished = false;
		public void run() {
			while (true) {
				System.out.println(String.format("Lines: %010d", LoadStat.getInstance().getCount()));
				
				if (finished) {
					System.out.println("Load Complete !!");
					break;
				}
				
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {}
				
				if (threadCnt.get() == 0) {
					finished = true;
				} else {
					System.out.println(threadCnt.get() + " threads remaining..");
				}
			}
		}
	}
}