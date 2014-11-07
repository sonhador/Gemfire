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