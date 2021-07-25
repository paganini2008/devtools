/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

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
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * LineWriter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class LineWriter extends BufferedWriter {

	public LineWriter(Writer out) throws IOException {
		this(out, 10);
	}
	
	public LineWriter(Writer out, int batchSize) {
		super(out);
		this.batchSize = batchSize;
	}

	private final AtomicInteger rows = new AtomicInteger(0);
	private final List<String> queue = new CopyOnWriteArrayList<String>();
	private final int batchSize;

	public void write(String str) throws IOException {
		queue.add(str);
		if (rows.incrementAndGet() > batchSize) {
			doWrite();
		}
	}

	public void flush() throws IOException {
		doWrite();
		super.flush();
	}

	private void doWrite() throws IOException {
		for (String line : queue) {
			super.write(line);
			super.newLine();
			queue.remove(line);
		}
	}

}
