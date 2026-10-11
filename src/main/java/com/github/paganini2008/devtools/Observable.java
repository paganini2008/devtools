/**
* Copyright 2017-2026 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.github.paganini2008.devtools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.PriorityBlockingQueue;


/**
 * 
 * Observable
 * 
 * A topic-based observable. Observers subscribe to a topic (or the default topic) and are notified
 * in ascending order. A repeatable observable keeps its observers after a notification; an
 * unrepeatable one notifies each observer only once and then removes it.
 *
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public class Observable {

	public static final String DEFAULT_TOPIC = "default";
	private final ConcurrentMap<String, ObserverGroup> groups = new ConcurrentHashMap<String, ObserverGroup>();
	private final boolean repeated;

	protected Observable(boolean repeated) {
		this.repeated = repeated;
	}

	static class ObserverGroup implements Observer {

		private final BlockingQueue<Observer> observers = new PriorityBlockingQueue<Observer>();
		private final String topic;
		private final boolean repeated;

		ObserverGroup(String topic, boolean repeated) {
			this.topic = topic;
			this.repeated = repeated;
		}

		public synchronized void addObserver(Observer observer) {
			if (observer != null) {
				if (!observers.contains(observer)) {
					observers.offer(observer);
				}
			}
		}

		/**
		 * Unsubscribes an observer from a topic.
		 */
		public void deleteObserver(Observer observer) {
			if (observer != null) {
				observers.remove(observer);
			}
		}

		/**
		 * Returns the number of observers of all topics.
		 */
		public int countOfObservers() {
			return observers.size();
		}

		public void clear() {
			observers.clear();
		}

		public void update(Observable ob, Object arg) {
			List<Observer> q = new ArrayList<Observer>();
			if (repeated) {
				q.addAll(observers);
				Collections.sort(q);
			} else {
				observers.drainTo(q);
			}
			for (Observer o : q) {
				o.update(ob, arg);
			}
			if (observers.isEmpty()) {
				ob.groups.remove(topic, this);
			}
		}

	}

	/**
	 * Subscribes an observer to the default topic.
	 *
	 * @throws IllegalArgumentException if the observer is null
	 */
	public void addObserver(Observer ob) {
		addObserver(DEFAULT_TOPIC, ob);
	}

	/**
	 * Subscribes an observer to a topic; an observer already subscribed is not added twice.
	 *
	 * @throws IllegalArgumentException if the observer is null
	 */
	public void addObserver(String topic, Observer ob) {
		Assert.isNull(ob, "Observer must not be null.");
		ObserverGroup obs = groups.computeIfAbsent(topic, t -> new ObserverGroup(t, repeated));
		obs.addObserver(ob);
	}

	/**
	 * Removes all observers of a topic.
	 */
	public void deleteObservers(String topic) {
		ObserverGroup obs = groups.remove(topic);
		if (obs != null) {
			obs.clear();
		}
	}

	/**
	 * Unsubscribes an observer from a topic.
	 */
	public void deleteObserver(String topic, Observer ob) {
		ObserverGroup obs = groups.get(topic);
		if (obs != null) {
			obs.deleteObserver(ob);
			if (obs.countOfObservers() == 0) {
				groups.remove(topic, obs);
			}
		}
	}

	/**
	 * Notifies the observers of the default topic with a null argument.
	 */
	public void notifyObservers() {
		notifyObservers(null);
	}

	/**
	 * Notifies the observers of the default topic.
	 */
	public void notifyObservers(Object arg) {
		notifyObservers(DEFAULT_TOPIC, arg);
	}

	/**
	 * Notifies the observers of a topic; does nothing if the topic has no observers.
	 */
	public void notifyObservers(String topic, Object arg) {
		ObserverGroup obs = groups.get(topic);
		if (obs != null) {
			obs.update(this, arg);
		}
	}

	/**
	 * Removes all observers of all topics.
	 */
	public void clearObservers() {
		for (String topic : groups.keySet()) {
			deleteObservers(topic);
		}
	}

	/**
	 * Returns the number of observers of all topics.
	 */
	public int countOfObservers() {
		int n = 0;
		for (ObserverGroup obs : groups.values()) {
			n += obs.countOfObservers();
		}
		return n;
	}

	/**
	 * Checks whether the topic has observers.
	 */
	public boolean hasTopic(String topicName) {
		return groups.containsKey(topicName);
	}

	/**
	 * Creates an observable whose observers are kept after notification.
	 */
	public static Observable repeatable() {
		return new Observable(true);
	}

	/**
	 * Creates an observable whose observers are notified only once.
	 */
	public static Observable unrepeatable() {
		return new Observable(false);
	}
}
