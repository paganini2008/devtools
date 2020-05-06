package com.github.paganini2008.devtools.multithreads;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.paganini2008.devtools.date.DateUtils;
import com.github.paganini2008.devtools.event.Event;
import com.github.paganini2008.devtools.event.EventBus;
import com.github.paganini2008.devtools.event.EventSubscriber;

/**
 * 
 * Clock
 *
 * @author Fred Feng
 * @version 1.0
 */
public final class Clock implements Executable {

	private static final String DEFAULT_DATE_FORMAT = "yyyyMMddHHmmss";
	private static final long REMAING_TIME_ADJUSTMENT = 10;
	private final AtomicBoolean running = new AtomicBoolean();
	private final ConcurrentMap<String, ClockTask> tasks = new ConcurrentHashMap<String, ClockTask>();
	private final EventBus<ClockEvent, String> eventBus;

	public Clock() {
		this(Executors.newCachedThreadPool());
	}

	public Clock(Executor executor) {
		eventBus = new EventBus<ClockEvent, String>(executor, true);
		running.set(true);
		ThreadUtils.scheduleAtFixedRate(this, 1, TimeUnit.SECONDS);
	}

	public void schedule(final ClockTask task, final long delay, final TimeUnit timeUnit) {
		long future = System.currentTimeMillis() + DateUtils.convertToMillis(delay, timeUnit);
		String datetime = DateUtils.format(future, DEFAULT_DATE_FORMAT);
		tasks.put(datetime + ":" + task.getTaskId(), task);
		eventBus.subscribe(new EventSubscriber<ClockEvent, String>() {
			public void onEventFired(ClockEvent event) {
				final ClockTask target = tasks.remove(event.getArgument() + ":" + task.getTaskId());
				if (target != null) {
					target.run();
					eventBus.unsubscribe(this);
				}
			}
		});
	}

	public void schedule(final ClockTask task, final long delay, final long period, final TimeUnit timeUnit) {
		long future = System.currentTimeMillis() + DateUtils.convertToMillis(delay, timeUnit);
		String datetime = DateUtils.format(future, DEFAULT_DATE_FORMAT);
		tasks.put(datetime + ":" + task.getTaskId(), task);
		eventBus.subscribe(new EventSubscriber<ClockEvent, String>() {

			public void onEventFired(ClockEvent event) {
				final ClockTask target = tasks.remove(event.getArgument() + ":" + task.getTaskId());
				if (target != null) {
					target.run();
					if (!target.isCancelled()) {
						doRepeat(target, period, timeUnit);
					}
					eventBus.unsubscribe(this);
				}

			}
		});
	}

	public void scheduleAtFixedRate(final ClockTask task, final long delay, final long period, final TimeUnit timeUnit) {
		long future = System.currentTimeMillis() + DateUtils.convertToMillis(delay, timeUnit);
		String datetime = DateUtils.format(future, DEFAULT_DATE_FORMAT);
		tasks.put(datetime + ":" + task.getTaskId(), task);
		eventBus.subscribe(new EventSubscriber<ClockEvent, String>() {

			public void onEventFired(ClockEvent event) {
				final ClockTask target = tasks.remove(event.getArgument() + ":" + task.getTaskId());
				if (target != null) {
					long startTime = System.currentTimeMillis();
					target.run();
					if (!target.isCancelled()) {
						long elapsed = System.currentTimeMillis() - startTime;
						long remaining = DateUtils.convertToMillis(period, timeUnit) - elapsed;
						if (remaining <= 0) {
							remaining = REMAING_TIME_ADJUSTMENT;
						}
						doRepeatAtFixedRate(target, remaining, DateUtils.convertToMillis(period, timeUnit));
					}
					eventBus.unsubscribe(this);
				}

			}
		});
	}

	void doRepeat(final ClockTask task, final long delay, final TimeUnit timeUnit) {
		if (!isRunning()) {
			return;
		}
		long future = System.currentTimeMillis() + DateUtils.convertToMillis(delay, timeUnit);
		String datetime = DateUtils.format(future, DEFAULT_DATE_FORMAT);
		tasks.put(datetime + ":" + task.getTaskId(), task);
		eventBus.subscribe(new EventSubscriber<ClockEvent, String>() {

			public void onEventFired(ClockEvent event) {
				final ClockTask target = tasks.remove(event.getArgument() + ":" + task.getTaskId());
				if (target != null) {
					target.run();
					if (!target.isCancelled()) {
						doRepeat(target, delay, timeUnit);
					}
					eventBus.unsubscribe(this);
				}
			}
		});
	}

	void doRepeatAtFixedRate(final ClockTask task, final long remaining, final long delay) {
		if (!isRunning()) {
			return;
		}
		long future = System.currentTimeMillis() + remaining;
		String datetime = DateUtils.format(future, DEFAULT_DATE_FORMAT);
		tasks.put(datetime + ":" + task.getTaskId(), task);
		eventBus.subscribe(new EventSubscriber<ClockEvent, String>() {

			public void onEventFired(ClockEvent event) {
				final ClockTask target = tasks.remove(event.getArgument() + ":" + task.getTaskId());
				if (target != null) {
					long startTime = System.currentTimeMillis();
					target.run();
					if (!target.isCancelled()) {
						long elapsed = System.currentTimeMillis() - startTime;
						long remaining = delay - elapsed;
						if (remaining <= 0) {
							remaining = REMAING_TIME_ADJUSTMENT;
						}
						doRepeatAtFixedRate(target, remaining, delay);
					}
					eventBus.unsubscribe(this);
				}
			}
		});
	}

	public boolean execute() {
		String now = DateUtils.format(System.currentTimeMillis(), DEFAULT_DATE_FORMAT);
		eventBus.publish(new ClockEvent(this, now));
		return running.get();
	}

	public boolean isRunning() {
		return running.get();
	}

	public void stop() {
		running.set(false);
		tasks.clear();
		eventBus.close();
	}

	public static abstract class ClockTask implements Runnable {

		private final String taskId;
		private boolean cancelled;

		protected ClockTask() {
			this.taskId = UUID.randomUUID().toString();
		}

		public boolean isCancelled() {
			return cancelled;
		}

		public boolean cancel() {
			this.cancelled = true;
			return true;
		}

		public String getTaskId() {
			return taskId;
		}

		public void run() {
			if (!isCancelled()) {
				runTask();
			}
		}

		protected abstract void runTask();

	}

	public static class ClockEvent extends Event<String> implements Cloneable {

		ClockEvent(Object source, String datetime) {
			super(source, datetime);
		}

		public Clock getSource() {
			return (Clock) super.getSource();
		}

		public ClockEvent clone() {
			try {
				return (ClockEvent) super.clone();
			} catch (CloneNotSupportedException e) {
				throw new IllegalStateException(e);
			}
		}

		private static final long serialVersionUID = 1L;

	}

	public static void main(String[] args) {
		Clock clock = new Clock();
		clock.schedule(new ClockTask() {
			protected void runTask() {
				System.out.println("Test1: " + DateUtils.format(System.currentTimeMillis()));
			}
		}, 2, 1, TimeUnit.SECONDS);
		clock.scheduleAtFixedRate(new ClockTask() {
			protected void runTask() {
				ThreadUtils.randomSleep(1000);
				System.out.println("Test2: " + DateUtils.format(System.currentTimeMillis()));
			}
		}, 5, 2, TimeUnit.SECONDS);
		clock.scheduleAtFixedRate(new ClockTask() {
			protected void runTask() {
				ThreadUtils.randomSleep(1000, 4000);
				System.out.println("Test3: " + DateUtils.format(System.currentTimeMillis()));
			}
		}, 10, 5, TimeUnit.SECONDS);
		clock.schedule(new ClockTask() {
			protected void runTask() {
				System.out.println("Test4: " + DateUtils.format(System.currentTimeMillis()));
			}
		}, 10, TimeUnit.SECONDS);
		clock.schedule(new ClockTask() {
			protected void runTask() {
				System.out.println("Test5: " + DateUtils.format(System.currentTimeMillis()));
			}
		}, 15, TimeUnit.SECONDS);
	}

}
