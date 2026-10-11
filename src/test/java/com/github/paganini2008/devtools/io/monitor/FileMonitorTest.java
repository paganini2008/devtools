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
package com.github.paganini2008.devtools.io.monitor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * FileMonitorTest
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class FileMonitorTest {

	@TempDir
	File root;

	static abstract class Adapter implements FileChangeListener {

		public void onDirectoryCreate(FileEntry fileEntry) {
		}

		public void onFileCreate(FileEntry fileEntry) {
		}

		public void onDirectoryDelete(FileEntry fileEntry) {
		}

		public void onFileDelete(FileEntry fileEntry) {
		}

		public void onDirectoryUpdate(FileEntry lastFileEntry, FileEntry fileEntry) {
		}

		public void onFileUpdate(FileEntry lastFileEntry, FileEntry fileEntry) {
		}
	}

	@Test
	public void testDetectsNewFile() throws Exception {
		final CountDownLatch created = new CountDownLatch(1);
		FileWatcher watcher = new FileWatcher(root, -1, null);
		watcher.addListeners(new Adapter() {
			public void onFileCreate(FileEntry fileEntry) {
				if ("new.txt".equals(fileEntry.getName())) {
					created.countDown();
				}
			}
		});
		final AtomicInteger before = new AtomicInteger();
		final AtomicInteger after = new AtomicInteger();
		FileMonitor monitor = new FileMonitor(20, TimeUnit.MILLISECONDS) {
			protected void beforeRunning() {
				before.incrementAndGet();
			}

			protected void afterRunning() {
				after.incrementAndGet();
			}
		};
		monitor.addWatchers(watcher);
		monitor.start();
		try {
			assertTrue(monitor.isRunning());
			assertThrows(IllegalStateException.class, () -> monitor.start());
			Files.write(new File(root, "new.txt").toPath(), new byte[] { 1 });
			assertTrue(created.await(5, TimeUnit.SECONDS));
			assertTrue(before.get() > 0);
		} finally {
			monitor.stop();
		}
		assertFalse(monitor.isRunning());
		assertThrows(IllegalStateException.class, () -> monitor.stop());
		assertFalse(monitor.execute());
	}

	@Test
	public void testExecutorAndRestart() throws Exception {
		final CountDownLatch latch = new CountDownLatch(2);
		FileWatcher watcher = new FileWatcher(root, -1, null);
		watcher.addListeners(new Adapter() {
			public void onFileCreate(FileEntry fileEntry) {
				latch.countDown();
			}
		});
		FileMonitor monitor = new FileMonitor(20, TimeUnit.MILLISECONDS);
		ExecutorService executor = Executors.newSingleThreadExecutor();
		monitor.setExecutor(executor);
		monitor.addWatchers(watcher);
		monitor.start();
		Files.write(new File(root, "a.txt").toPath(), new byte[] { 1 });
		awaitCount(latch, 1);
		monitor.stop();
		assertTrue(executor.isShutdown());

		monitor.setExecutor(null);
		monitor.start();
		try {
			Files.write(new File(root, "b.txt").toPath(), new byte[] { 1 });
			assertTrue(latch.await(5, TimeUnit.SECONDS));
		} finally {
			monitor.stop();
		}
	}

	private static void awaitCount(CountDownLatch latch, long expected) throws InterruptedException {
		long deadline = System.currentTimeMillis() + 5000;
		while (latch.getCount() > expected && System.currentTimeMillis() < deadline) {
			Thread.sleep(5);
		}
		assertEquals(expected, latch.getCount());
	}

	@Test
	public void testRemoveWatchersAndErrors() throws IOException {
		FileWatcher watcher = new FileWatcher(root, -1, null);
		final AtomicInteger calls = new AtomicInteger();
		watcher.addListeners(new Adapter() {
			public void onStart(FileWatcher fileWatcher) {
				calls.incrementAndGet();
			}
		});
		watcher.start();
		FileMonitor monitor = new FileMonitor(1, TimeUnit.HOURS);
		monitor.addWatchers(watcher);
		monitor.addWatchers((FileWatcher[]) null);
		// not running: execute does nothing
		assertFalse(monitor.execute());
		assertEquals(0, calls.get());

		monitor.removeWatchers(watcher);
		monitor.removeWatchers((FileWatcher[]) null);
		monitor.start();
		try {
			assertTrue(monitor.execute());
			assertEquals(0, calls.get());
			// errors do not stop a running monitor
			assertTrue(monitor.onError(new IllegalStateException("test")));
		} finally {
			monitor.stop();
		}
		assertFalse(monitor.onError(new IllegalStateException("test")));
		monitor.onCancellation(null);

		assertThrows(IllegalArgumentException.class, () -> new FileMonitor(0, TimeUnit.SECONDS));
		assertThrows(IllegalArgumentException.class, () -> new FileMonitor(1, null));
	}

}
