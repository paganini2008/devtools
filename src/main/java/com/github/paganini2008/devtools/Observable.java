package com.github.paganini2008.devtools;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * 
 * Observable
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @version 1.0
 */
public class Observable {

	private static final String DEFAULT_TOPIC = "default";
	private final ConcurrentMap<String, Observers> groups = new ConcurrentHashMap<String, Observers>();
	private boolean repeated = true;

	public Observable() {
	}

	static class Observers implements Observer {

		private final Queue<Observer> observers = new PriorityBlockingQueue<Observer>();
		private final boolean repeated;

		Observers(boolean repeated) {
			this.repeated = repeated;
		}

		public void addObserver(Observer observer) {
			if (observer != null) {
				if (!observers.contains(observer)) {
					observers.offer(observer);
				}
			}
		}

		public void deleteObserver(Observer observer) {
			if (observer != null) {
				observers.remove(observer);
			}
		}

		public int countOfObservers() {
			return observers.size();
		}

		public void clear() {
			observers.clear();
		}

		public void update(Observable ob, Object arg) {
			final Queue<Observer> q = new ArrayDeque<Observer>(observers);
			Observer o;
			while (!q.isEmpty()) {
				o = q.poll();
				o.update(ob, arg);
				if (!repeated) {
					observers.remove(o);
				}
			}
		}

	}

	public void setRepeated(boolean repeated) {
		this.repeated = repeated;
	}

	public void addObserver(Observer ob) {
		addObserver(DEFAULT_TOPIC, ob);
	}

	public void addObserver(String topic, Observer ob) {
		Assert.isNull(ob, "Observer must not be null.");
		Observers obs = groups.get(topic);
		if (obs == null) {
			groups.putIfAbsent(topic, new Observers(repeated));
			obs = groups.get(topic);
		}
		obs.addObserver(ob);
	}

	public void deleteObservers(String topic) {
		if (groups.containsKey(topic)) {
			Observers obs = groups.remove(topic);
			if (obs != null) {
				obs.clear();
			}
		}
	}

	public void deleteObserver(String topic, Observer ob) {
		if (groups.containsKey(topic)) {
			Observers obs = groups.get(topic);
			if (obs != null) {
				obs.deleteObserver(ob);
				if (obs.countOfObservers() == 0) {
					groups.remove(topic);
				}
			}
		}
	}

	public void notifyObservers() {
		notifyObservers(null);
	}

	public void notifyObservers(Object arg) {
		notifyObservers(DEFAULT_TOPIC, arg);
	}

	public void notifyObservers(String topic, Object arg) {
		if (groups.containsKey(topic)) {
			groups.get(topic).update(this, arg);
		}
	}

	public void clearObservers() {
		for (String topic : groups.keySet()) {
			deleteObservers(topic);
		}
	}

	public int countOfObservers() {
		int n = 0;
		for (Observers obs : groups.values()) {
			n += obs.countOfObservers();
		}
		return n;
	}

	public static Observable repeatable() {
		Observable o = new Observable();
		o.setRepeated(true);
		return o;
	}

	public static Observable unrepeatable() {
		Observable o = new Observable();
		o.setRepeated(false);
		return o;
	}
}
