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

import java.util.concurrent.atomic.AtomicLong;

public class LoadStat {
	private static LoadStat instance = null;
	private static Object obj = new Object();
	
	private AtomicLong count;
	
	private long startTime = -1L;
	private long endTime;
	
	private LoadStat() {
		count = new AtomicLong(0);
	}
	
	private void startMeasuringTime() {
		if (startTime == -1L) {
			startTime = System.currentTimeMillis();
		}
	}
	
	public void endMeasuringTime() {
		endTime = System.currentTimeMillis();
	}
	
	public long getSecondsTook() {
		return (endTime - startTime) / 1000;
	}
	
	public long getCount() {
		return count.get();
	}
	
	public long increment(long amount) {
		startMeasuringTime();
		return count.addAndGet(amount);
	}
	
	public long increment() {
		startMeasuringTime();
		return count.incrementAndGet();
	}
	
	public void decrement() {
		count.decrementAndGet();
	}
	
	public static LoadStat getInstance() {
		if (instance == null) {
			synchronized (obj) {
				if (instance == null) {
					instance = new LoadStat();
				}
			}
		}
		
		return instance;
	}
}
