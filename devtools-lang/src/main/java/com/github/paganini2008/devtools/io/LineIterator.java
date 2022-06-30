/**
* Copyright 2017-2022 Fred Feng (paganini.fy@gmail.com)

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
 * LineIterator
 * 
 * @author Fred Feng
 * @since 2.0.1
 */
public class LineIterator implements Iterator<String> {

	private final BufferedReader br;
	private String cachedLine;
	private boolean finished = false;

	public LineIterator(File input, String charset) throws IOException {
		this(FileUtils.openInputStream(input), charset);
	}

	public LineIterator(InputStream ins, String charset) {
		this(IOUtils.getBufferedReader(ins, CharsetUtils.toCharset(charset)));
	}

	public LineIterator(Reader reader) {
		br = IOUtils.getBufferedReader(reader);
	}

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

	protected boolean isValid(String line) {
		return true;
	}

	public String next() {
		return nextLine();
	}

	public String nextLine() {
		if (!hasNext()) {
			throw new NoSuchElementException("No more lines.");
		}
		String currentLine = cachedLine;
		cachedLine = null;
		return currentLine;
	}

	public void close() {
		finished = true;
		IOUtils.closeQuietly(br);
		cachedLine = null;
	}

	public void remove() {
		throw new UnsupportedOperationException("Remove unsupported on LineIterator");
	}

}
