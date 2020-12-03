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
 * @author Jimmy Hoff
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
