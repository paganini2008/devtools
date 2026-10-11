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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * FileWatcherTest
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class FileWatcherTest {

	@TempDir
	File root;

	/**
	 * Records the events as strings like "fileCreate:a.txt"
	 */
	static class RecordingListener implements FileChangeListener {

		final List<String> events = Collections.synchronizedList(new ArrayList<String>());
		int starts;
		int ends;

		public void onStart(FileWatcher fileWatcher) {
			starts++;
		}

		public void onEnd(FileWatcher fileWatcher) {
			ends++;
		}

		public void onDirectoryCreate(FileEntry fileEntry) {
			events.add("dirCreate:" + fileEntry.getName());
		}

		public void onFileCreate(FileEntry fileEntry) {
			events.add("fileCreate:" + fileEntry.getName());
		}

		public void onDirectoryDelete(FileEntry fileEntry) {
			events.add("dirDelete:" + fileEntry.getName());
		}

		public void onFileDelete(FileEntry fileEntry) {
			events.add("fileDelete:" + fileEntry.getName());
		}

		public void onDirectoryUpdate(FileEntry lastFileEntry, FileEntry fileEntry) {
			events.add("dirUpdate:" + fileEntry.getName());
		}

		public void onFileUpdate(FileEntry lastFileEntry, FileEntry fileEntry) {
			events.add("fileUpdate:" + fileEntry.getName());
		}

		List<String> drain() {
			synchronized (events) {
				List<String> copy = new ArrayList<String>(events);
				events.clear();
				return copy;
			}
		}

		List<String> drainIgnoringDirectoryUpdates() {
			List<String> result = new ArrayList<String>();
			for (String event : drain()) {
				if (!event.startsWith("dirUpdate:")) {
					result.add(event);
				}
			}
			return result;
		}
	}

	private static void write(File file, String content) throws IOException {
		Files.write(file.toPath(), content.getBytes(StandardCharsets.UTF_8));
	}

	@Test
	public void testCreateUpdateDelete() throws IOException {
		File a = new File(root, "a.txt");
		write(a, "a");
		FileWatcher watcher = new FileWatcher(root, -1, null);
		RecordingListener listener = new RecordingListener();
		watcher.addListeners(listener);
		watcher.start();
		assertSame(root, watcher.getDirectory());
		assertEquals(1, watcher.getRootEntry().getChildEntries().size());

		watcher.checkAndNotify();
		assertTrue(listener.drain().isEmpty());
		assertEquals(1, listener.starts);
		assertEquals(1, listener.ends);

		File b = new File(root, "b.txt");
		write(b, "b");
		watcher.checkAndNotify();
		assertEquals(Arrays.asList("fileCreate:b.txt"), listener.drainIgnoringDirectoryUpdates());

		write(a, "changed content");
		watcher.checkAndNotify();
		assertEquals(Arrays.asList("fileUpdate:a.txt"), listener.drainIgnoringDirectoryUpdates());

		assertTrue(b.delete());
		watcher.checkAndNotify();
		assertEquals(Arrays.asList("fileDelete:b.txt"), listener.drainIgnoringDirectoryUpdates());

		// deleting the last remaining file must also be reported
		assertTrue(a.delete());
		watcher.checkAndNotify();
		assertEquals(Arrays.asList("fileDelete:a.txt"), listener.drainIgnoringDirectoryUpdates());
		assertTrue(watcher.getRootEntry().getChildEntries().isEmpty());
	}

	@Test
	public void testNestedDirectories() throws IOException {
		FileWatcher watcher = new FileWatcher(root, -1, null);
		RecordingListener listener = new RecordingListener();
		watcher.addListeners(listener);
		watcher.start();

		File sub = new File(root, "sub");
		File deep = new File(sub, "deep");
		assertTrue(deep.mkdirs());
		write(new File(deep, "x.txt"), "x");
		watcher.checkAndNotify();
		// parents are reported before their children
		assertEquals(Arrays.asList("dirCreate:sub", "dirCreate:deep", "fileCreate:x.txt"), listener.drainIgnoringDirectoryUpdates());

		File y = new File(deep, "y.txt");
		write(y, "y");
		watcher.checkAndNotify();
		assertEquals(Arrays.asList("fileCreate:y.txt"), listener.drainIgnoringDirectoryUpdates());

		// a directory becoming empty
		assertTrue(y.delete());
		assertTrue(new File(deep, "x.txt").delete());
		watcher.checkAndNotify();
		List<String> events = listener.drainIgnoringDirectoryUpdates();
		assertEquals(2, events.size());
		assertTrue(events.containsAll(Arrays.asList("fileDelete:x.txt", "fileDelete:y.txt")));

		write(new File(deep, "z.txt"), "z");
		watcher.checkAndNotify();
		listener.drain();
		// deleting a whole tree reports every entry, parent first
		assertTrue(new File(deep, "z.txt").delete());
		assertTrue(deep.delete());
		assertTrue(sub.delete());
		watcher.checkAndNotify();
		assertEquals(Arrays.asList("dirDelete:sub", "dirDelete:deep", "fileDelete:z.txt"), listener.drainIgnoringDirectoryUpdates());
	}

	@Test
	public void testDirectoryUpdate() throws IOException {
		File sub = new File(root, "sub");
		assertTrue(sub.mkdir());
		FileWatcher watcher = new FileWatcher(root, -1, null);
		RecordingListener listener = new RecordingListener();
		watcher.addListeners(listener);
		watcher.start();
		assertTrue(sub.setLastModified(sub.lastModified() - 100000));
		watcher.checkAndNotify();
		assertEquals(Arrays.asList("dirUpdate:sub"), listener.drain());
	}

	@Test
	public void testDepth() throws IOException {
		File level1 = new File(root, "level1");
		File level2 = new File(level1, "level2");
		assertTrue(level2.mkdirs());
		FileWatcher watcher = new FileWatcher(root, 0, null);
		RecordingListener listener = new RecordingListener();
		watcher.addListeners(listener);
		watcher.start();
		FileEntry level1Entry = watcher.getRootEntry().getChildEntries().get(level1.getAbsolutePath());
		assertNotNull(level1Entry);
		assertEquals(1, level1Entry.getDepth());
		assertTrue(level1Entry.getChildEntries().isEmpty());

		write(new File(level2, "ignored.txt"), "x");
		write(new File(level1, "watched.txt"), "x");
		write(new File(root, "top.txt"), "x");
		watcher.checkAndNotify();
		assertEquals(Arrays.asList("fileCreate:top.txt"), listener.drainIgnoringDirectoryUpdates());

		FileWatcher deeper = new FileWatcher(root, 1, null);
		deeper.start();
		FileEntry entry = deeper.getRootEntry().getChildEntries().get(level1.getAbsolutePath());
		FileEntry level2Entry = entry.getChildEntries().get(level2.getAbsolutePath());
		assertNotNull(level2Entry);
		assertNotNull(entry.getChildEntries().get(new File(level1, "watched.txt").getAbsolutePath()));
		assertEquals(2, level2Entry.getDepth());
		assertTrue(level2Entry.getChildEntries().isEmpty());
	}

	@Test
	public void testFilter() throws IOException {
		FileWatcher watcher = new FileWatcher(root, -1, file -> file.getName().endsWith(".txt"));
		RecordingListener listener = new RecordingListener();
		watcher.addListeners(listener);
		watcher.start();
		write(new File(root, "a.txt"), "a");
		write(new File(root, "b.log"), "b");
		watcher.checkAndNotify();
		assertEquals(Arrays.asList("fileCreate:a.txt"), listener.drainIgnoringDirectoryUpdates());
	}

	@Test
	public void testRootCreatedAndDeleted() throws IOException {
		File dir = new File(root, "watched");
		FileWatcher watcher = new FileWatcher(dir, -1, null);
		RecordingListener listener = new RecordingListener();
		watcher.addListeners(listener);
		watcher.start();
		assertFalse(watcher.getRootEntry().isExists());

		assertTrue(dir.mkdir());
		write(new File(dir, "a.txt"), "a");
		watcher.checkAndNotify();
		assertEquals(Arrays.asList("dirCreate:watched", "fileCreate:a.txt"), listener.drain());

		assertTrue(new File(dir, "a.txt").delete());
		assertTrue(dir.delete());
		watcher.checkAndNotify();
		assertEquals(Arrays.asList("dirDelete:watched", "fileDelete:a.txt"), listener.drain());
	}

	@Test
	public void testWatchSingleFile() throws IOException {
		File file = new File(root, "single.txt");
		FileWatcher watcher = new FileWatcher(file, -1, null);
		RecordingListener listener = new RecordingListener();
		watcher.addListeners(listener);
		watcher.start();
		write(file, "1");
		watcher.checkAndNotify();
		assertEquals(Arrays.asList("fileCreate:single.txt"), listener.drain());
		write(file, "12");
		watcher.checkAndNotify();
		assertEquals(Arrays.asList("fileUpdate:single.txt"), listener.drain());
		assertTrue(file.delete());
		watcher.checkAndNotify();
		assertEquals(Arrays.asList("fileDelete:single.txt"), listener.drain());
	}

	@Test
	public void testRefreshWithoutStartAndRemoveListener() throws IOException {
		FileWatcher watcher = new FileWatcher(root, -1, null);
		RecordingListener listener = new RecordingListener();
		watcher.addListeners(listener);
		watcher.addListeners((FileChangeListener[]) null);
		// first refresh only takes a snapshot
		write(new File(root, "a.txt"), "a");
		watcher.refresh();
		assertTrue(listener.drain().isEmpty());
		assertEquals(1, watcher.getRootEntry().getChildEntries().size());

		watcher.removeListeners(listener);
		watcher.removeListeners((FileChangeListener[]) null);
		write(new File(root, "b.txt"), "b");
		watcher.checkAndNotify();
		assertTrue(listener.drain().isEmpty());
		assertEquals(0, listener.starts);
	}

	@Test
	public void testLoggingListener() throws IOException {
		FileWatcher watcher = new FileWatcher(root, -1, null);
		watcher.addListeners(new LoggingFileChangeListener());
		watcher.start();
		File sub = new File(root, "sub");
		assertTrue(sub.mkdir());
		File a = new File(sub, "a.txt");
		write(a, "a");
		watcher.checkAndNotify();
		write(a, "abc");
		assertTrue(sub.setLastModified(sub.lastModified() - 100000));
		watcher.checkAndNotify();
		assertTrue(a.delete());
		assertTrue(sub.delete());
		watcher.checkAndNotify();
		assertTrue(watcher.getRootEntry().getChildEntries().isEmpty());
	}

	@Test
	public void testIllegalArguments() {
		assertThrows(IllegalArgumentException.class, () -> new FileWatcher(null, -1, null));
		assertThrows(IllegalArgumentException.class, () -> new FileEntry(null));
	}

}
