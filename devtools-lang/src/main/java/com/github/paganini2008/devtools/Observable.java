/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

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

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.PriorityBlockingQueue;

import com.github.paganini2008.devtools.collection.MapUtils;

/**
 * 
 * Observable
 *
 * @author Fred Feng
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

		private final Queue<Observer> observers = new PriorityBlockingQueue<Observer>();
		private final String topic;
		private final boolean repeated;

		ObserverGroup(String topic, boolean repeated) {
			this.topic = topic;
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
			if (observers.size() == 1) {
				Observer o = repeated ? observers.peek() : observers.poll();
				o.update(ob, arg);
			} else if (observers.size() > 1) {
				Queue<Observer> q = new ArrayDeque<Observer>(observers);
				Observer o;
				while (q.size() > 0) {
					o = q.poll();
					o.update(ob, arg);
					if (!repeated) {
						observers.remove(o);
					}
				}
			}
			if (observers.isEmpty()) {
				ob.deleteObservers(topic);
			}
		}

	}

	public void addObserver(Observer ob) {
		addObserver(DEFAULT_TOPIC, ob);
	}

	public void addObserver(String topic, Observer ob) {
		Assert.isNull(ob, "Observer must not be null.");
		ObserverGroup obs = MapUtils.get(groups, topic, () -> {
			return new ObserverGroup(topic, repeated);
		});
		obs.addObserver(ob);
	}

	public void deleteObservers(String topic) {
		if (groups.containsKey(topic)) {
			ObserverGroup obs = groups.remove(topic);
			if (obs != null) {
				obs.clear();
			}
		}
	}

	public void deleteObserver(String topic, Observer ob) {
		if (groups.containsKey(topic)) {
			ObserverGroup obs = groups.get(topic);
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
		for (ObserverGroup obs : groups.values()) {
			n += obs.countOfObservers();
		}
		return n;
	}

	public boolean hasTopic(String topicName) {
		return groups.containsKey(topicName);
	}

	public static Observable repeatable() {
		return new Observable(true);
	}

	public static Observable unrepeatable() {
		return new Observable(false);
	}
}
