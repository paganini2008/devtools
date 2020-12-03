package com.github.paganini2008.devtools.io;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * StringArrayWriter
 * 
 * @author Jimmy Hoff
 * 
 * 
 * @version 1.0
 */
public class StringArrayWriter extends Writer {

	private final List<String> lines = new ArrayList<String>();

	public void write(String str) {
		lines.add(str);
	}

	public void write(char[] cbuf, int off, int len) throws IOException {
		lines.add(new String(cbuf, off, len));
	}

	public void flush() throws IOException {
	}

	public void close() throws IOException {
		lines.clear();
	}

	public String[] toArray() {
		return lines.toArray(new String[0]);
	}

}
