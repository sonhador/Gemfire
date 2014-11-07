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
		public void run() {
			while (true) {
				System.out.println(String.format("Lines: %010d", LoadStat.getInstance().getCount()));
				
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {}
				
				if (threadCnt.get() == 0) {
					System.out.println("Load Complete !!");
					break;
				} else {
					System.out.println(threadCnt.get() + " threads remaining..");
				}
			}
		}
	}
}