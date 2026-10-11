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

import static com.github.paganini2008.devtools.CharsetUtils.UTF_8;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * Tails files: every complete line appended to a tracked file is passed to {@link #handleLine(File, String)}, similar to
 * {@code tail -f}.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public class FileTracker implements FileWatcher {

	private final FileWatchdog watchdog;
	private final Map<File, RandomAccessFile> objects = new HashMap<File, RandomAccessFile>();
	private final Map<File, Long> pointers = new HashMap<File, Long>();

	/**
	 * Tracks the files; content existing at construction time is skipped, only lines appended later are reported.
	 */
	public FileTracker(File... files) {
		this.watchdog = new FileWatchdog(files);
		this.watchdog.addWatcher(this);

		refresh(files);
	}

	private Charset charset = UTF_8;

	/**
	 * Sets the charset used to decode lines (UTF-8 by default, also used for {@code null}).
	 */
	public void setCharset(Charset charset) {
		this.charset = charset != null ? charset : UTF_8;
	}

	/**
	 * Starts watching the files in the background, see {@link FileWatchdog#setInterval(long)}.
	 */
	public void watch(int interval) {
		watchdog.setInterval(interval);
		watchdog.start();
	}

	/**
	 * Stops watching and closes all files.
	 */
	public synchronized void cancel() {
		if (watchdog.isRunning()) {
			watchdog.stop();
		}
		for (RandomAccessFile raf : objects.values()) {
			IOUtils.closeQuietly(raf);
		}
		objects.clear();
		pointers.clear();
	}

	FileWatchdog getWatchdog() {
		return watchdog;
	}

	private synchronized void refresh(File[] files) {
		if (files == null) {
			return;
		}
		for (File file : files) {
			if (file != null && file.exists()) {
				try {
					RandomAccessFile raf = new RandomAccessFile(file, "r");
					IOUtils.closeQuietly(objects.put(file, raf));
					pointers.put(file, raf.length());
				} catch (IOException e) {
					cancel();
					throw new IllegalStateException(e);
				}
			}
		}
	}

	public synchronized void onCreate(File file) {
		try {
			RandomAccessFile raf = new RandomAccessFile(file, "r");
			IOUtils.closeQuietly(objects.put(file, raf));
			pointers.put(file, 0L);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public synchronized void onDelete(File file) {
		IOUtils.closeQuietly(objects.remove(file));
		pointers.remove(file);
	}

	public synchronized void onUpdate(File file) {
		RandomAccessFile raf = objects.get(file);
		if (raf == null) {
			onCreate(file);
			raf = objects.get(file);
		}
		try {
			readLines(file, raf);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Reads every complete line (terminated by '\n') appended since the last read. A trailing incomplete line is kept
	 * until its line terminator is written. If the file has been truncated, it is read again from the beginning.
	 */
	private void readLines(File file, RandomAccessFile raf) throws IOException {
		Long saved = pointers.get(file);
		long pointer = saved != null ? saved.longValue() : 0L;
		long length = raf.length();
		if (length < pointer) {
			pointer = 0L;
		}
		if (length == pointer) {
			pointers.put(file, pointer);
			return;
		}
		byte[] bytes = new byte[(int) Math.min(length - pointer, Integer.MAX_VALUE)];
		raf.seek(pointer);
		raf.readFully(bytes);
		int lineStart = 0;
		for (int i = 0; i < bytes.length; i++) {
			if (bytes[i] == '\n') {
				int lineEnd = i;
				if (lineEnd > lineStart && bytes[lineEnd - 1] == '\r') {
					lineEnd--;
				}
				handleLine(file, new String(bytes, lineStart, lineEnd - lineStart, charset));
				lineStart = i + 1;
			}
		}
		pointers.put(file, pointer + lineStart);
	}

	/**
	 * Receives every complete line (without line terminator) appended to a tracked file.
	 */
	protected void handleLine(File file, String line) {
	}
}
