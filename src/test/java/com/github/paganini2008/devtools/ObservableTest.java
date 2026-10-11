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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * ObservableTest
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class ObservableTest {

	static class Recorder implements Observer {

		final List<String> log;
		final String name;
		final int priority;

		Recorder(List<String> log, String name, int priority) {
			this.log = log;
			this.name = name;
			this.priority = priority;
		}

		@Override
		public void update(Observable ob, Object arg) {
			log.add(name + ":" + arg);
		}

		@Override
		public int compareTo(Observer other) {
			return Integer.compare(priority, ((Recorder) other).priority);
		}
	}

	@Test
	public void testUnrepeatableNotifiesOnceInPriorityOrder() {
		List<String> log = new ArrayList<String>();
		Observable observable = Observable.unrepeatable();
		observable.addObserver(new Recorder(log, "c", 3));
		observable.addObserver(new Recorder(log, "a", 1));
		observable.addObserver(new Recorder(log, "b", 2));
		assertEquals(3, observable.countOfObservers());
		observable.notifyObservers("x");
		assertEquals(Arrays.asList("a:x", "b:x", "c:x"), log);
		assertFalse(observable.hasTopic(Observable.DEFAULT_TOPIC));
		assertEquals(0, observable.countOfObservers());
		observable.notifyObservers("y");
		assertEquals(3, log.size());
	}

	@Test
	public void testUnrepeatableSingleObserver() {
		List<String> log = new ArrayList<String>();
		Observable observable = Observable.unrepeatable();
		observable.addObserver(new Recorder(log, "a", 1));
		observable.notifyObservers();
		observable.notifyObservers();
		assertEquals(Arrays.asList("a:null"), log);
	}

	@Test
	public void testRepeatableNotifiesEveryTimeInPriorityOrder() {
		List<String> log = new ArrayList<String>();
		Observable observable = Observable.repeatable();
		for (int i = 5; i > 0; i--) {
			observable.addObserver(new Recorder(log, String.valueOf(i), i));
		}
		observable.notifyObservers(1);
		observable.notifyObservers(2);
		assertEquals(Arrays.asList("1:1", "2:1", "3:1", "4:1", "5:1", "1:2", "2:2", "3:2", "4:2", "5:2"), log);
		assertEquals(5, observable.countOfObservers());
	}

	@Test
	public void testTopicsAndRemoval() {
		List<String> log = new ArrayList<String>();
		Observable observable = Observable.repeatable();
		Recorder a = new Recorder(log, "a", 1);
		Recorder b = new Recorder(log, "b", 2);
		observable.addObserver("t1", a);
		observable.addObserver("t1", a);
		observable.addObserver("t2", b);
		assertEquals(2, observable.countOfObservers());
		assertTrue(observable.hasTopic("t1"));
		observable.notifyObservers("t2", "z");
		assertEquals(Arrays.asList("b:z"), log);
		observable.deleteObserver("t1", a);
		assertFalse(observable.hasTopic("t1"));
		observable.deleteObserver("missing", a);
		observable.deleteObservers("missing");
		observable.deleteObservers("t2");
		assertFalse(observable.hasTopic("t2"));
		observable.addObserver("t3", a);
		observable.addObserver("t4", b);
		observable.clearObservers();
		assertEquals(0, observable.countOfObservers());
		observable.notifyObservers("t3", "none");
		assertEquals(1, log.size());
	}

	@Test
	public void testObserverReceivesObservable() {
		Observable observable = Observable.repeatable();
		Observable[] holder = new Observable[1];
		observable.addObserver((ob, arg) -> holder[0] = ob);
		observable.notifyObservers();
		assertSame(observable, holder[0]);
		assertThrows(IllegalArgumentException.class, () -> observable.addObserver(null));
	}

}
