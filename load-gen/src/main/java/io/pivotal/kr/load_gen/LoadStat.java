package io.pivotal.kr.load_gen;

import java.util.concurrent.atomic.AtomicLong;

public class LoadStat {
	private static LoadStat instance = null;
	private static Object obj = new Object();
	
	private AtomicLong count;
	
	private LoadStat() {
		count = new AtomicLong(0);
	}
	
	public long getCount() {
		return count.get();
	}
	
	public long increment(long amount) {
		return count.addAndGet(amount);
	}
	
	public long increment() {
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
