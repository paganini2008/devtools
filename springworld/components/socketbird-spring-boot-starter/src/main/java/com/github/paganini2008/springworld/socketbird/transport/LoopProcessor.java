package com.github.paganini2008.springworld.socketbird.transport;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.github.paganini2008.devtools.multithreads.ThreadUtils;
import com.github.paganini2008.springworld.socketbird.Tuple;
import com.github.paganini2008.springworld.socketbird.store.Store;

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
	private Store store;

	@Value("${socketbird.store.collection.name}")
	private String collection;

	private final List<Handler> handlers = new CopyOnWriteArrayList<Handler>();
	private final AtomicBoolean running = new AtomicBoolean(false);
	private Thread runner;

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

	public void start() {
		running.set(true);
		runner = ThreadUtils.runAsThread(this);
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
			if (store == null) {
				continue;
			}
			Tuple tuple = store.get(collection);
			if (tuple != null) {
				for (Handler handler : handlers) {
					handler.onData(tuple);
				}
			} else {
				ThreadUtils.randomSleep(1000L);
			}
		}
		log.info("Ending Loop");
	}

}
