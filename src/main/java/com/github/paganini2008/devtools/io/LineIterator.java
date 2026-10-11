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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.github.paganini2008.devtools.CharsetUtils;

/**
 * Iterator over the lines of a reader. Call {@link #close()} when the iteration is stopped early.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public class LineIterator implements Iterator<String> {

	private final BufferedReader br;
	private String cachedLine;
	private boolean finished = false;

	/**
	 * Iterates the lines of the file decoded with the charset (the platform default if {@code null}).
	 *
	 * @throws java.io.FileNotFoundException if the file does not exist
	 */
	public LineIterator(File input, String charset) throws IOException {
		this(FileUtils.openInputStream(input), charset);
	}

	public LineIterator(InputStream ins, String charset) {
		this(IOUtils.getBufferedReader(ins, CharsetUtils.toCharset(charset)));
	}

	public LineIterator(Reader reader) {
		br = IOUtils.getBufferedReader(reader);
	}

	/**
	 * Tests whether another valid line exists.
	 *
	 * @throws IllegalStateException if reading fails; the reader is closed in that case
	 */
	public boolean hasNext() {
		if (cachedLine != null) {
			return true;
		} else if (finished) {
			return false;
		} else {
			try {
				while (true) {
					String line = br.readLine();
					if (line == null) {
						finished = true;
						return false;
					} else if (isValid(line)) {
						cachedLine = line;
						return true;
					}
				}
			} catch (IOException ioe) {
				close();
				throw new IllegalStateException(ioe);
			}
		}
	}

	/**
	 * Decides whether a line is returned; lines are skipped if this returns {@code false}. All lines are valid by default.
	 */
	protected boolean isValid(String line) {
		return true;
	}

	public String next() {
		return nextLine();
	}

	/**
	 * Returns the next valid line.
	 *
	 * @throws java.util.NoSuchElementException if there are no more lines
	 */
	public String nextLine() {
		if (!hasNext()) {
			throw new NoSuchElementException("No more lines.");
		}
		String currentLine = cachedLine;
		cachedLine = null;
		return currentLine;
	}

	/**
	 * Closes the underlying reader; afterwards {@link #hasNext()} returns {@code false}.
	 */
	public void close() {
		finished = true;
		IOUtils.closeQuietly(br);
		cachedLine = null;
	}

	public void remove() {
		throw new UnsupportedOperationException("Remove unsupported on LineIterator");
	}

}
