package com.github.paganini2008.devtools.event;

import java.util.UUID;

public class TestMain {

	static class TestEvent extends Event {

		public TestEvent(Object source, String msg) {
			super(source);
			this.msg = msg;
		}

		private final String msg;

		public String getMsg() {
			return msg;
		}

		private static final long serialVersionUID = 2380494907997490663L;

	}

	static class TestSubcriber implements EventSubscriber<TestEvent> {

		private final String name;

		public TestSubcriber(String name) {
			this.name = name;
		}

		public void onEventFired(TestEvent event) {
			System.out.println("Name: " + name + "\t" + event.getMsg());
			EventBus source = (EventBus) event.getSource();
			source.subscribe(this);
		}

	}

	public static void main(String[] args) throws Exception {
		EventBus eventBus = new EventBus(10);
		for (int i = 0; i < 10; i++) {
			eventBus.subscribe(new TestSubcriber("Name:" + i));
		}
		System.in.read();

		for(int i=0;i<10000;i++) {
			eventBus.publish(new TestEvent(eventBus, UUID.randomUUID().toString()));
			System.out.println("*****************: " + i);
			//ThreadUtils.randomSleep(1000L);
		}
		eventBus.close();
		System.out.println("TestMain.main()");
	}

}
