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
 * @author Jimmy Hoff
 * @version 1.0
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
