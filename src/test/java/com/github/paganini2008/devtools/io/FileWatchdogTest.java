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
package com.github.paganini2008.devtools.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

/**
 * FileWatchdogTest
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
@Timeout(value = 10, unit = TimeUnit.SECONDS)
public class FileWatchdogTest {

	@TempDir
	File tempDir;

	private static class RecordingWatcher implements FileWatcher {

		final List<String> events = Collections.synchronizedList(new ArrayList<String>());

		@Override
		public void onCreate(File file) {
			events.add("create:" + file.getName());
		}

		@Override
		public void onDelete(File file) {
			events.add("delete:" + file.getName());
		}

		@Override
		public void onUpdate(File file) {
			events.add("update:" + file.getName());
		}
	}

	private static void append(File file, String text, Charset charset) throws IOException {
		Files.write(file.toPath(), text.getBytes(charset), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
	}

	@Test
	public void run_detectsCreateUpdateAndDelete() throws IOException {
		File file = new File(tempDir, "w.txt");
		FileWatchdog watchdog = new FileWatchdog(file);
		RecordingWatcher watcher = new RecordingWatcher();
		watchdog.addWatcher(watcher);
		watchdog.addWatcher(null);

		watchdog.run();
		assertTrue(watcher.events.isEmpty());

		append(file, "a", StandardCharsets.UTF_8);
		watchdog.run();
		append(file, "bb", StandardCharsets.UTF_8);
		watchdog.run();
		watchdog.run();
		assertTrue(file.delete());
		watchdog.run();
		assertEquals(Arrays.asList("create:w.txt", "update:w.txt", "delete:w.txt"), watcher.events);
	}

	@Test
	public void run_detectsNewerTimestampWithSameLength() throws IOException {
		File file = new File(tempDir, "t.txt");
		append(file, "x", StandardCharsets.UTF_8);
		assertTrue(file.setLastModified(10_000L));
		FileWatchdog watchdog = new FileWatchdog();
		watchdog.addFiles(file);
		watchdog.addFiles((File[]) null);
		RecordingWatcher watcher = new RecordingWatcher();
		watchdog.addWatcher(watcher);
		assertTrue(file.setLastModified(20_000L));
		watchdog.run();
		assertEquals(Collections.singletonList("update:t.txt"), watcher.events);
	}

	@Test
	public void run_reportsWatcherFailuresToOnError() throws IOException {
		File a = new File(tempDir, "a.txt");
		File b = new File(tempDir, "b.txt");
		final List<String> errors = new ArrayList<String>();
		FileWatchdog watchdog = new FileWatchdog(a, b) {
			@Override
			protected boolean onError(File file, IOException e) {
				errors.add(file.getName() + ":" + e.getMessage());
				return true;
			}
		};
		watchdog.addWatcher(new FileWatcher() {
			@Override
			public void onCreate(File file) throws IOException {
				if (file.getName().equals("a.txt")) {
					throw new IOException("io");
				}
				throw new IllegalStateException("runtime");
			}

			@Override
			public void onDelete(File file) {
			}

			@Override
			public void onUpdate(File file) {
			}
		});
		append(a, "1", StandardCharsets.UTF_8);
		append(b, "1", StandardCharsets.UTF_8);
		watchdog.run();
		assertEquals(Arrays.asList("a.txt:io", "b.txt:runtime"), errors);
	}

	@Test
	public void run_stopsIteratingWhenOnErrorReturnsFalse() throws IOException {
		File a = new File(tempDir, "a.txt");
		File b = new File(tempDir, "b.txt");
		final List<String> created = new ArrayList<String>();
		FileWatchdog watchdog = new FileWatchdog(a, b) {
			@Override
			protected boolean onError(File file, IOException e) {
				return false;
			}
		};
		watchdog.addWatcher(new RecordingWatcher() {
			@Override
			public void onCreate(File file) {
				created.add(file.getName());
				throw new IllegalStateException("fail");
			}
		});
		append(a, "1", StandardCharsets.UTF_8);
		append(b, "1", StandardCharsets.UTF_8);
		watchdog.run();
		assertEquals(Collections.singletonList("a.txt"), created);

		FileWatchdog ioFailing = new FileWatchdog(new File(tempDir, "c.txt"), new File(tempDir, "d.txt")) {
			@Override
			protected boolean onError(File file, IOException e) {
				return false;
			}
		};
		final List<String> seen = new ArrayList<String>();
		ioFailing.addWatcher(new RecordingWatcher() {
			@Override
			public void onCreate(File file) {
				seen.add(file.getName());
				sneakyThrow(new IOException("io"));
			}
		});
		append(new File(tempDir, "c.txt"), "1", StandardCharsets.UTF_8);
		append(new File(tempDir, "d.txt"), "1", StandardCharsets.UTF_8);
		ioFailing.run();
		assertEquals(Collections.singletonList("c.txt"), seen);
	}

	@SuppressWarnings("unchecked")
	private static <E extends Throwable> void sneakyThrow(Throwable e) throws E {
		throw (E) e;
	}

	@Test
	public void defaultOnError_continues() throws IOException {
		File a = new File(tempDir, "a.txt");
		File b = new File(tempDir, "b.txt");
		FileWatchdog watchdog = new FileWatchdog(a, b);
		final List<String> created = new ArrayList<String>();
		watchdog.addWatcher(new RecordingWatcher() {
			@Override
			public void onCreate(File file) {
				created.add(file.getName());
				throw new IllegalStateException("fail");
			}
		});
		append(a, "1", StandardCharsets.UTF_8);
		append(b, "1", StandardCharsets.UTF_8);
		watchdog.run();
		assertEquals(Arrays.asList("a.txt", "b.txt"), created);
	}

	@Test
	public void startAndStop_canBeRestarted() {
		FileWatchdog watchdog = new FileWatchdog(new File(tempDir, "x"));
		assertFalse(watchdog.isRunning());
		assertThrows(IllegalStateException.class, watchdog::stop);
		watchdog.start();
		assertTrue(watchdog.isRunning());
		assertThrows(IllegalStateException.class, watchdog::start);
		watchdog.stop();
		assertFalse(watchdog.isRunning());
		watchdog.start();
		assertTrue(watchdog.isRunning());
		watchdog.stop();
	}

	@Test
	public void setInterval_ignoresValuesBelowDefault() {
		FileWatchdog watchdog = new FileWatchdog();
		assertEquals(3000L, watchdog.getInterval());
		watchdog.setInterval(100L);
		assertEquals(3000L, watchdog.getInterval());
		watchdog.setInterval(5000L);
		assertEquals(5000L, watchdog.getInterval());
	}

	@Test
	public void fileEntry_reportsState() throws IOException {
		File file = new File(tempDir, "e.txt");
		FileWatchdog.FileEntry entry = new FileWatchdog.FileEntry(file);
		assertFalse(entry.isExists());
		assertEquals(0, entry.getLastModified());
		assertEquals(file, entry.getFile());
		assertTrue(entry.toString().contains("exists=false"));
		append(file, "abc", StandardCharsets.UTF_8);
		assertEquals(1, entry.changed());
		assertTrue(entry.isExists());
		assertEquals(0, entry.changed());
	}

	// ---------------------------------------------------------------- FileTracker

	private static class RecordingTracker extends FileTracker {

		final List<String> lines = new ArrayList<String>();

		RecordingTracker(File... files) {
			super(files);
		}

		@Override
		protected void handleLine(File file, String line) {
			lines.add(line);
		}
	}

	@Test
	public void tracker_emitsOnlyAppendedCompleteLines() throws IOException {
		File file = new File(tempDir, "log.txt");
		append(file, "old line\n", StandardCharsets.UTF_8);
		RecordingTracker tracker = new RecordingTracker(file);
		try {
			append(file, "first\nsec", StandardCharsets.UTF_8);
			tracker.onUpdate(file);
			assertEquals(Collections.singletonList("first"), tracker.lines);
			append(file, "ond\r\nthird\n", StandardCharsets.UTF_8);
			tracker.onUpdate(file);
			assertEquals(Arrays.asList("first", "second", "third"), tracker.lines);
			tracker.onUpdate(file);
			assertEquals(3, tracker.lines.size());
		} finally {
			tracker.cancel();
		}
	}

	@Test
	public void tracker_decodesConfiguredCharset() throws IOException {
		File file = new File(tempDir, "gbk.log");
		append(file, "", StandardCharsets.UTF_8);
		RecordingTracker tracker = new RecordingTracker(file);
		try {
			tracker.setCharset(Charset.forName("GBK"));
			append(file, "中文日志\n", Charset.forName("GBK"));
			tracker.onUpdate(file);
			assertEquals(Collections.singletonList("中文日志"), tracker.lines);
			tracker.setCharset(null);
			append(file, "汉字\n", StandardCharsets.UTF_8);
			tracker.onUpdate(file);
			assertEquals("汉字", tracker.lines.get(1));
		} finally {
			tracker.cancel();
		}
	}

	@Test
	public void tracker_handlesCreateDeleteAndTruncate() throws IOException {
		File file = new File(tempDir, "late.txt");
		RecordingTracker tracker = new RecordingTracker(file, null);
		try {
			append(file, "aaaa\n", StandardCharsets.UTF_8);
			tracker.onCreate(file);
			assertTrue(tracker.lines.isEmpty());
			tracker.onUpdate(file);
			assertEquals(Collections.singletonList("aaaa"), tracker.lines);

			// truncated file is read again from the beginning
			Files.write(file.toPath(), "z\n".getBytes(StandardCharsets.UTF_8));
			tracker.onUpdate(file);
			assertEquals(Arrays.asList("aaaa", "z"), tracker.lines);

			tracker.onDelete(file);
			append(file, "b\n", StandardCharsets.UTF_8);
			tracker.onUpdate(file);
			assertEquals(Arrays.asList("aaaa", "z", "z", "b"), tracker.lines);
		} finally {
			tracker.cancel();
		}
	}

	@Test
	public void tracker_onCreateOfMissingFileThrows() {
		RecordingTracker tracker = new RecordingTracker();
		assertThrows(IllegalStateException.class, () -> tracker.onCreate(new File(tempDir, "missing")));
		tracker.cancel();
	}

	@Test
	public void tracker_watchStartsAndCancelStopsWatchdog() throws IOException {
		File file = new File(tempDir, "watch.txt");
		append(file, "", StandardCharsets.UTF_8);
		RecordingTracker tracker = new RecordingTracker(file);
		tracker.watch(5000);
		assertTrue(tracker.getWatchdog().isRunning());
		assertEquals(5000L, tracker.getWatchdog().getInterval());
		tracker.cancel();
		assertFalse(tracker.getWatchdog().isRunning());
		tracker.cancel();
	}

	@Test
	public void tracker_receivesEventsThroughWatchdog() throws IOException {
		File file = new File(tempDir, "via.txt");
		append(file, "", StandardCharsets.UTF_8);
		RecordingTracker tracker = new RecordingTracker(file);
		try {
			append(file, "line\n", StandardCharsets.UTF_8);
			tracker.getWatchdog().run();
			assertEquals(Collections.singletonList("line"), tracker.lines);
		} finally {
			tracker.cancel();
		}
	}

	@Test
	public void tracker_doesNotLeakFileHandles() throws Exception {
		final File file = new File(tempDir, "leak.txt");
		append(file, "x\n", StandardCharsets.UTF_8);
		IoTestSupport.assertNoFileHandleLeak(() -> {
			RecordingTracker tracker = new RecordingTracker(file);
			tracker.onCreate(file);
			tracker.onDelete(file);
			tracker.onUpdate(file);
			tracker.cancel();
		});
	}
}
