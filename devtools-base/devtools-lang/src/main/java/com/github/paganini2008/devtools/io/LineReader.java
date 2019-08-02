package com.github.paganini2008.devtools.io;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.github.paganini2008.devtools.CharsetUtils;

public class LineReader implements Iterator<List<String>> {

	public LineReader(File file, int rows) throws IOException {
		this.raf = new RandomAccessFile(file, "r");
		this.offset = raf.getFilePointer();
		this.rows = rows;
	}

	private final RandomAccessFile raf;
	private final int rows;
	private long offset;
	private Charset charset = CharsetUtils.UTF_8;

	public void setCharset(Charset charset) {
		this.charset = charset;
	}

	public boolean hasNext() {
		try {
			String line = raf.readLine();
			raf.seek(offset);
			return line != null;
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public List<String> next() {
		List<String> lines = new ArrayList<String>();
		int row = 0;
		String line;
		try {
			while (row++ < rows && null != (line = raf.readLine())) {
				lines.add(new String(line.getBytes("ISO-8859-1"), charset));
			}
			this.offset = raf.getFilePointer();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		return lines;
		
	}

	public void close() {
		try {
			raf.close();
		} catch (IOException e) {
		}
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

	public static void main(String[] args) throws Exception {
		LineReader reader = new LineReader(new File("d:/company_status.txt"), 10);
		while (reader.hasNext()) {
			List<String> lines = reader.next();
			for (String line : lines) {
				System.out.println(line);
			}
			System.out.println("----------------------------------");
		}
		reader.close();
	}

}
