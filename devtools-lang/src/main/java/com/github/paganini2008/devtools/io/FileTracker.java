/**
* Copyright 2021 Fred Feng (paganini.fy@gmail.com)

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
 * FileTracker
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class FileTracker implements FileWatcher {

	private static final int SKIP_LINE_BEGIN = System.getProperty("line.separator").length();
	private final FileWatchdog watchdog;
	private final Map<File, RandomAccessFile> objects = new HashMap<File, RandomAccessFile>();
	private final Map<File, Long> pointers = new HashMap<File, Long>();

	public FileTracker(File... files) {
		this.watchdog = new FileWatchdog(files);
		this.watchdog.addWatcher(this);

		refresh(files);
	}

	private Charset charset = UTF_8;

	public void setCharset(Charset charset) {
		this.charset = charset;
	}

	public void watch(int interval) {
		watchdog.setInterval(interval);
		watchdog.start();
	}

	public void cancel() {
		watchdog.stop();
	}

	private void refresh(File[] files) {
		for (File file : files) {
			if (file.exists()) {
				try {
					RandomAccessFile raf = new RandomAccessFile(file, "r");
					objects.put(file, raf);
					pointers.put(file, raf.length());
				} catch (IOException e) {
					throw new IllegalStateException(e);
				}
			}
		}
	}

	public synchronized void onCreate(File file) {
		try {
			RandomAccessFile raf = new RandomAccessFile(file, "r");
			objects.put(file, raf);
			pointers.put(file, 0L);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public synchronized void onDelete(File file) {
		objects.remove(file);
		pointers.remove(file);
	}

	public synchronized void onUpdate(File file) {
		RandomAccessFile raf = objects.get(file);
		long pointer = pointers.get(file);
		try {
			raf.seek(pointer + SKIP_LINE_BEGIN);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		try {
			String line;
			while (null != (line = raf.readLine())) {
				handleLine(file, new String(line.getBytes("ISO-8859-1"), charset));
			}
			pointers.put(file, raf.getFilePointer());
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	protected void handleLine(File file, String line) {
	}

	public static void main(String[] args) throws Exception {
		FileTracker tail = new FileTracker(new File("d:/sql/solr.txt")) {

			protected void handleLine(File file, String line) {
				System.out.println("Line: " + line);
			}

		};
		// tail.setCharset(CharsetUtils.GB_2312);
		tail.watch(500);
		System.in.read();
		tail.cancel();
		System.out.println("FileTracker.main()");
	}

}
