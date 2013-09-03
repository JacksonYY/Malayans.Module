package iot.mike.malayans.module.threadpool;

import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPool {
	private static ThreadPoolExecutor threadPoolExecutor 				= null;
	
	private static class ThreadPoolHolder {
		public static ThreadPool instance 			= new ThreadPool();
	}
	
	private ThreadPool() {
		LinkedBlockingDeque<Runnable> taskQueue	= 
				new LinkedBlockingDeque<Runnable>();
		threadPoolExecutor = 
				new ThreadPoolExecutor(5000, 
						10000, 
						50, TimeUnit.MINUTES, 
						taskQueue);
	}
	
	public static ThreadPool getInstance() {
		return ThreadPoolHolder.instance;
	}
	
	public void execute(Runnable runnable) {
		threadPoolExecutor.execute(runnable);
	}
	
	public long getTaskCount() {
		return threadPoolExecutor.getTaskCount();
	}
	
	@SuppressWarnings("rawtypes")
	public Future submit(Runnable runnable) {
		return threadPoolExecutor.submit(runnable);
	}
	
	public int getActiveCount() {
		return threadPoolExecutor.getActiveCount();
	}
}
