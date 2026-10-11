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
package com.github.paganini2008.devtools.multithreads;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import com.github.paganini2008.devtools.multithreads.latch.CounterLatch;
import com.github.paganini2008.devtools.multithreads.latch.Latch;

/**
 * ThreadFactoryTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
@Timeout(10)
public class ThreadFactoryTest {

	@Test
	public void defaultThreadFactory_createsNormalNonDaemonThreads() {
		DefaultThreadFactory factory = new DefaultThreadFactory("worker");
		Thread t = factory.newThread(() -> {
		});
		assertTrue(t.getName().startsWith("worker-thread-"));
		assertFalse(t.isDaemon());
		assertEquals(Thread.NORM_PRIORITY, t.getPriority());
		assertEquals(Thread.State.NEW, t.getState());
		assertTrue(new DefaultThreadFactory().newThread(() -> {
		}).getName().startsWith("thread-"));
		assertTrue(new DefaultThreadFactory(" ").newThread(() -> {
		}).getName().startsWith("thread-"));
	}

	@Test
	public void defaultThreadFactory_normalizesDaemonAndPriorityInheritedFromGroup() throws Exception {
		ThreadGroup group = new ThreadGroup("custom-group");
		group.setMaxPriority(Thread.MAX_PRIORITY);
		DefaultThreadFactory factory = new DefaultThreadFactory();
		factory.setThreadGroup(group);
		UncaughtExceptionHandler handler = (t, e) -> {
		};
		factory.setExceptionHandler(handler);
		Thread[] created = new Thread[1];
		Thread parent = new Thread(() -> created[0] = factory.newThread(() -> {
		}));
		parent.setDaemon(true);
		parent.setPriority(Thread.MAX_PRIORITY);
		parent.start();
		parent.join();
		assertSame(group, created[0].getThreadGroup());
		assertFalse(created[0].isDaemon());
		assertEquals(Thread.NORM_PRIORITY, created[0].getPriority());
		assertSame(handler, created[0].getUncaughtExceptionHandler());
	}

	@Test
	public void defaultThreadFactory_defaultHandlerPrintsStackTrace() throws Exception {
		PrintStream err = System.err;
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		System.setErr(new PrintStream(buffer, true, "UTF-8"));
		try {
			Thread t = new DefaultThreadFactory().newThread(() -> {
				throw new IllegalStateException("printed");
			});
			t.start();
			t.join();
		} finally {
			System.setErr(err);
		}
		assertTrue(buffer.toString("UTF-8").contains("printed"));
	}

	@Test
	public void pooledThreadFactory_numbersPools() {
		Thread a = new PooledThreadFactory().newThread(() -> {
		});
		Thread b = new PooledThreadFactory("my-").newThread(() -> {
		});
		assertTrue(a.getName().matches("pool-\\d+-thread-\\d+"), a.getName());
		assertTrue(b.getName().matches("my-\\d+-thread-\\d+"), b.getName());
	}

	@Test
	public void threadFactoryBuilder_appliesAllSettings() {
		UncaughtExceptionHandler handler = (t, e) -> {
		};
		ThreadFactoryBuilder builder = new ThreadFactoryBuilder().setNameFormat("job-%d").setDaemon(true).setPriority(Thread.MAX_PRIORITY)
				.setUncaughtExceptionHandler(handler).setBackingThreadFactory(r -> new Thread(r));
		assertEquals("job-%d", builder.getNameFormat());
		assertEquals(Boolean.TRUE, builder.getDaemon());
		assertEquals(Integer.valueOf(Thread.MAX_PRIORITY), builder.getPriority());
		assertSame(handler, builder.getUncaughtExceptionHandler());
		ThreadFactory factory = builder.build();
		Thread t0 = factory.newThread(() -> {
		});
		Thread t1 = factory.newThread(() -> {
		});
		assertEquals("job-0", t0.getName());
		assertEquals("job-1", t1.getName());
		assertTrue(t0.isDaemon());
		assertEquals(Thread.MAX_PRIORITY, t0.getPriority());
		assertSame(handler, t0.getUncaughtExceptionHandler());
	}

	@Test
	public void threadFactoryBuilder_defaultsAndValidation() {
		ThreadFactoryBuilder builder = new ThreadFactoryBuilder();
		assertNull(builder.getBackingThreadFactory());
		assertNull(builder.setPriority(null).getPriority());
		Thread t = builder.build().newThread(() -> {
		});
		assertTrue(t.getName().startsWith("thread-"));
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> builder.setPriority(11));
		assertEquals("Thread priority (11) must be <= 10", e.getMessage());
		e = assertThrows(IllegalArgumentException.class, () -> builder.setPriority(0));
		assertEquals("Thread priority (0) must be >= 1", e.getMessage());
		ThreadFactory nullFactory = new ThreadFactoryBuilder().setBackingThreadFactory(r -> null).build();
		IllegalStateException ise = assertThrows(IllegalStateException.class, () -> nullFactory.newThread(() -> {
		}));
		assertEquals("Null thread object!", ise.getMessage());
	}

	@Test
	public void threadPoolBuilder_buildsConfiguredPool() throws Exception {
		Latch latch = new CounterLatch(2);
		ThreadFactory factory = GenericThreadPoolTest.SILENT;
		ThreadPoolBuilder builder = ThreadPoolBuilder.common(2).setLatch(latch).setQueueSize(5).setTimeout(0).setThreadFactory(factory);
		assertEquals(2, builder.getMaxPoolSize());
		assertSame(latch, builder.getLatch());
		assertEquals(5, builder.getQueueSize());
		assertEquals(0, builder.getTimeout());
		assertSame(factory, builder.getThreadFactory());
		ThreadPool pool = builder.build();
		try {
			CountDownLatch done = new CountDownLatch(1);
			pool.execute(done::countDown);
			assertTrue(done.await(5, TimeUnit.SECONDS));
			assertEquals(2, pool.getMaxPoolSize());
		} finally {
			pool.shutdown();
		}
		ThreadPool unlimited = new ThreadPoolBuilder().setMaxPoolSize(1).setMaxPermits(0).build();
		ThreadPool limited = new ThreadPoolBuilder().setMaxPoolSize(1).setMaxPermits(1).build();
		unlimited.shutdown();
		limited.shutdown();
		assertTrue(unlimited.isShutdown());
	}

	@Test
	public void clockTask_runsUntilCancelled() {
		int[] runs = new int[1];
		ClockTask task = new ClockTask() {
			protected void runTask() {
				runs[0]++;
			}
		};
		assertEquals(36, task.getTaskId().length());
		task.run();
		assertFalse(task.isCancelled());
		assertTrue(task.cancel());
		task.run();
		assertTrue(task.isCancelled());
		assertEquals(1, runs[0]);
	}

	@Test
	public void interfaceDefaults() throws Throwable {
		Action<String> action = () -> "x";
		assertTrue(action.shouldReact("x"));
		assertFalse(action.shouldReact(null));
		assertNull(action.onReaction("x", null));
		Executable executable = () -> true;
		executable.onCancellation(null);
		assertTrue(executable.execute());
		PrintStream err = System.err;
		System.setErr(new PrintStream(new ByteArrayOutputStream()));
		try {
			action.onFailure(new Exception("quiet"), null);
			assertFalse(executable.onError(new Exception("quiet")));
		} finally {
			System.setErr(err);
		}
		Retryable retryable = () -> {
		};
		assertEquals(Integer.MAX_VALUE, retryable.getMaxRetries());
		assertEquals(Exception.class, retryable.captureClasses()[0]);
		retryable.onError(1, new Exception());
	}

}
