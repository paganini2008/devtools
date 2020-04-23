package com.github.paganini2008.devtools.event;

import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * TestMain
 * 
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public class TestMain {

	public static class TestEvent extends Event<String> {

		private static final long serialVersionUID = 1L;

		public TestEvent(Object source, String msg) {
			super(source, msg);
		}

	}

	private static final AtomicInteger counter = new AtomicInteger();

	public static class TestSubcriber implements EventSubscriber<TestEvent, String> {

		private final String name;

		public TestSubcriber(String name) {
			this.name = name;
		}

		public void onEventFired(TestEvent event) {
			System.out.println("Name: " + name + ", Value: " + event.getArgument());
			counter.incrementAndGet();
			
			EventBus<TestEvent, String> eventBus = (EventBus<TestEvent, String>)event.getSource();
			//eventBus.subscribe(this);
		}

	}

	public static void main(String[] args) throws Exception {
		EventBus<TestEvent, String> eventBus = new EventBus<TestEvent, String>(Executors.newFixedThreadPool(8), false);
		for (int i = 0; i < 5; i++) {
			eventBus.subscribe(new TestSubcriber("Name_" + i));
		}
		for (int i = 0; i < 10; i++) {
			eventBus.publish(new TestEvent(eventBus, String.valueOf(i)));
		}
		System.in.read();
		eventBus.close();
		System.out.println("TestMain.main(): " + counter);
	}

}
