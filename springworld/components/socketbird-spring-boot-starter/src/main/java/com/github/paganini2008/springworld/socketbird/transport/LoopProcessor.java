package com.github.paganini2008.springworld.socketbird.transport;

import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.github.paganini2008.devtools.multithreads.Executable;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;
import com.github.paganini2008.springworld.socketbird.Counter;
import com.github.paganini2008.springworld.socketbird.Tuple;
import com.github.paganini2008.springworld.socketbird.buffer.BufferZone;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * LoopProcessor
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
@Slf4j
public class LoopProcessor implements Runnable {

	@Autowired
	private BufferZone bufferZone;

	@Autowired
	private Counter counter;

	@Value("${socketbird.store.collectionName}")
	private String collection;

	private final Queue<Handler> handlers = new PriorityBlockingQueue<Handler>();
	private final AtomicBoolean running = new AtomicBoolean(false);
	private Thread runner;
	private LoggingThread loggingThread = new LoggingThread();

	public void addHandler(Handler handler) {
		if (handler != null) {
			handlers.add(handler);
		}
	}

	public void removeHandler(Handler handler) {
		while (handlers.contains(handler)) {
			handlers.remove(handler);
		}
	}

	public int countOfHandlers() {
		return handlers.size();
	}

	public void startDaemon() {
		running.set(true);
		runner = ThreadUtils.runAsThread(this);
		loggingThread.start();
		log.info("LoopProcessor is started.");
	}

	public void stop() {
		running.set(false);
		if (runner != null) {
			try {
				runner.join();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			log.info("LoopProcessor is stoped.");
		}
	}

	@Override
	public void run() {
		while (running.get()) {
			if (bufferZone == null) {
				break;
			}
			Tuple tuple = null;
			try {
				tuple = bufferZone.get(collection);
			} catch (Exception e) {
				if (log.isTraceEnabled()) {
					log.trace(e.getMessage(), e);
				}
			}
			if (tuple != null) {
				counter.incrementCount();
				for (Handler handler : handlers) {
					handler.onData(tuple);
				}
			} else {
				ThreadUtils.randomSleep(1000L);
			}
		}
		log.info("Ending Loop!");
	}

	class LoggingThread implements Executable {

		public void start() {
			ThreadUtils.scheduleAtFixedRate(this, 3, TimeUnit.SECONDS);
		}

		@Override
		public boolean execute() {
			if (log.isTraceEnabled()) {
				try {
					log.trace("[Snapshot] count=" + counter.getLocal() + "/" + counter.get() + ", tps=" + counter.getLocalTps() + "/"
							+ counter.getTps() + ", buffer=" + bufferZone.size(collection));
				} catch (Exception ignored) {
				}
			}
			return running.get();
		}

	}

}
