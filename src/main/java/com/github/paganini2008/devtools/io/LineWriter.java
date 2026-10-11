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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link BufferedWriter} that queues the strings passed to {@link #write(String)} and writes them as separate lines in
 * batches. Queued lines are written on {@link #flush()} and {@link #close()}.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public class LineWriter extends BufferedWriter {

	/**
	 * Creates a writer writing queued lines in batches of 10.
	 */
	public LineWriter(Writer out) throws IOException {
		this(out, 10);
	}
	
	/**
	 * Creates a writer that writes the queued lines once more than {@code batchSize} lines are queued.
	 */
	public LineWriter(Writer out, int batchSize) {
		super(out);
		this.batchSize = batchSize;
	}

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	private final List<String> queue = new ArrayList<String>();
	private final int batchSize;

	/**
	 * Queues the string as a line.
	 */
	public void write(String str) throws IOException {
		synchronized (lock) {
			queue.add(str);
			if (queue.size() > batchSize) {
				doWrite();
			}
		}
	}

	public void flush() throws IOException {
		synchronized (lock) {
			doWrite();
			super.flush();
		}
	}

	public void close() throws IOException {
		synchronized (lock) {
			try {
				doWrite();
			} finally {
				super.close();
			}
		}
	}

	private void doWrite() throws IOException {
		if (queue.isEmpty()) {
			return;
		}
		try {
			for (String line : queue) {
				super.write(line, 0, line.length());
				// BufferedWriter.newLine() delegates to write(String) which is overridden here
				super.write(LINE_SEPARATOR, 0, LINE_SEPARATOR.length());
			}
		} finally {
			queue.clear();
		}
	}

}
