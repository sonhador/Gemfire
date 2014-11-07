package io.pivotal.kr.load_gen;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.ibatis.session.SqlSession;

public class GemfireXDLoadWorker extends TimerTask {
	private ResourceDescriptor resource;
	private String delimiter;
	private String mode;
	
	private BlockingQueue<String[]> dataQueue;
	
	private GemfireXDClient gemfireXDClient;
	
	private volatile boolean finishedFileRead = true;
	
	private int threadId;
	
	private GemfireXDLoadManager manager;
	
	public GemfireXDLoadWorker(GemfireXDLoadManager manager, int threadId, ResourceDescriptor resource, String delimiter, GemfireXDClient gemfireXDClient, String mode) throws FileNotFoundException {
		this.manager = manager;
		this.resource = resource;
		this.delimiter = delimiter;
		this.gemfireXDClient = gemfireXDClient;
		this.threadId = threadId;
		this.mode = mode;
	}
	
	public int getThreadId() {
		return threadId;
	}
	
	public void run() {
		SqlSession sqlSession = null;
		try {
			try {
				sqlSession = gemfireXDClient.getSession();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return;
			}
			
			new Thread(new Reader()).start();
			
			while (true) {
				String[] data = null;
				try {
					data = dataQueue.poll(100, TimeUnit.MILLISECONDS);
				} catch (InterruptedException e) {}
				
				if (data == null && finishedFileRead) {
					break;
				}
				
				if (gemfireXDClient.crud(mode, sqlSession, data, delimiter)) {	
					LoadStat.getInstance().increment(data.length);
				}
			}
		} finally {
			manager.countDownWorkDone();
			
			if (sqlSession != null) {
				gemfireXDClient.closeSession(sqlSession);
			}
		}
	}
	
	private class Reader implements Runnable {
		public Reader() {
			dataQueue = new LinkedBlockingQueue<String[]>(500);
		}
		
		public void run() {
			try {
				finishedFileRead = false;
				resource.openFile();

				boolean retry = false;
				String[] lines = null;
				while (true) {
					if (retry == false) {	
						lines = resource.readLines();
					}
						
					if (lines == null) {
						break;
					}
					
					try {
						dataQueue.add(lines);
						retry = false;
					} catch (IllegalStateException e) {
						try {
							Thread.sleep(30);
						} catch (InterruptedException e1) {
						}
						retry = true;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					resource.closeFile();
				} catch (IOException e) {}
				finishedFileRead = true;
			}
		}
	}
}