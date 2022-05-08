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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.CharsetUtils;
import com.github.paganini2008.devtools.StringUtils;

/**
 * General IO operations.
 * 
 * @author Fred Feng
 * @since 2.0.1
 */
public abstract class IOUtils {

	public static final int DEFAULT_BYTE_BUFFER_SIZE = 8 * 1024;

	public static final int DEFAULT_CHAR_BUFFER_SIZE = 8 * 1024;

	public static final int EOF = -1;

	public static final String NEWLINE = System.getProperty("line.separator");

	public static void write(byte[] content, OutputStream output) throws IOException {
		Assert.isNull(output, "OutputStream must not be null.");
		if (content != null) {
			output.write(content);
		}
	}

	public static void write(char[] content, Writer writer) throws IOException {
		Assert.isNull(writer, "Writer must not be null.");
		if (content != null) {
			writer.write(content);
		}
	}

	public static void write(CharSequence content, Writer writer) throws IOException {
		Assert.isNull(writer, "Writer must not be null.");
		if (StringUtils.isNotBlank(content)) {
			writer.write(content.toString());
		}
	}

	public static void writeLine(CharSequence content, Writer writer) throws IOException {
		Assert.isNull(writer, "Writer must not be null.");
		if (StringUtils.isNotBlank(content)) {
			writer.write(content.toString());
			writer.write(NEWLINE);
		}
	}

	public static void close(Closeable closeable) throws IOException {
		if (closeable != null) {
			closeable.close();
		}
	}

	public static void closeQuietly(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
			}
		}
	}

	public static void flushQuietly(OutputStream out) {
		if (out != null) {
			try {
				out.flush();
			} catch (IOException e) {
			}
		}
	}

	public static void flush(OutputStream out) throws IOException {
		if (out != null) {
			out.flush();
		}
	}

	public static void flushQuietly(Writer writer) {
		if (writer != null) {
			try {
				writer.flush();
			} catch (IOException e) {
			}
		}
	}

	public static void flush(Writer writer) throws IOException {
		if (writer != null) {
			writer.flush();
		}
	}

	public static void flushAndCloseQuietly(Writer writer) {
		flushQuietly(writer);
		closeQuietly(writer);
	}

	public static void flushAndCloseQuietly(OutputStream os) {
		flushQuietly(os);
		closeQuietly(os);
	}

	public static void flushAndClose(Writer writer) throws IOException {
		flushQuietly(writer);
		close(writer);
	}

	public static void flushAndClose(OutputStream os) throws IOException {
		flushQuietly(os);
		close(os);
	}

	public static void copyLines(Reader reader, Writer writer) throws IOException {
		Assert.isNull(reader, "Reader must not be null.");
		Assert.isNull(writer, "Writer must not be null.");
		BufferedReader br;
		try {
			br = getBufferedReader(reader);
			String s;
			while (null != (s = br.readLine())) {
				writer.write(s);
				writer.write(NEWLINE);
			}
		} finally {
			flushQuietly(writer);
		}
	}

	public static long copy(Reader reader, Writer writer) throws IOException {
		return copy(reader, writer, -1);
	}

	public static long copy(Reader reader, Writer writer, int maxSize) throws IOException {
		return copy(reader, writer, DEFAULT_CHAR_BUFFER_SIZE, maxSize);
	}

	public static long copy(Reader reader, Writer writer, int bufferSize, int maxSize) throws IOException {
		Assert.isNull(reader, "Reader must not be null.");
		Assert.isNull(writer, "Writer must not be null.");
		final boolean capped = maxSize > 0;
		int remaining = maxSize;
		char[] buffer = getCharBuffer(bufferSize);
		long length = 0;
		try {
			int read;
			while (EOF != (read = reader.read(buffer))) {
				if (capped) {
					if (read > remaining) {
						writer.write(buffer, 0, remaining);
						break;
					}
					remaining -= read;
				}
				writer.write(buffer, 0, read);
				length += read;
			}
		} finally {
			flushQuietly(writer);
		}
		return length;
	}

	private static byte[] getByteBuffer(int bufferSize) {
		if (bufferSize <= 0) {
			bufferSize = DEFAULT_BYTE_BUFFER_SIZE;
		}
		return new byte[bufferSize];
	}

	private static char[] getCharBuffer(int bufferSize) {
		if (bufferSize <= 0) {
			bufferSize = DEFAULT_BYTE_BUFFER_SIZE;
		}
		return new char[bufferSize];
	}

	public static long copy(InputStream input, OutputStream output) throws IOException {
		return copy(input, output, DEFAULT_BYTE_BUFFER_SIZE, -1);
	}

	public static long copy(InputStream input, OutputStream output, int maxSize) throws IOException {
		return copy(input, output, DEFAULT_BYTE_BUFFER_SIZE, maxSize);
	}

	public static long copy(InputStream input, final OutputStream output, int bufferSize, int maxSize) throws IOException {
		return copy(input, new StreamCopier() {

			@Override
			public void copy(byte[] bytes, int offset, int length) throws IOException {
				output.write(bytes, offset, length);
			}

		}, bufferSize, maxSize);
	}

	public static long copy(InputStream input, StreamCopier streamCopier, int bufferSize, int maxSize) throws IOException {
		Assert.isNull(input, "Input stream is null.");
		Assert.isNull(streamCopier, "Undefined streamCopier");
		final boolean capped = maxSize > 0;
		int remaining = maxSize;
		byte[] buffer = getByteBuffer(bufferSize);
		long length = 0;
		try {
			streamCopier.onStart();
			int read;
			while (EOF != (read = input.read(buffer))) {
				if (capped) {
					if (read > remaining) {
						streamCopier.copy(buffer, 0, remaining);
						break;
					}
					remaining -= read;
				}
				streamCopier.copy(buffer, 0, read);
				length += read;
				streamCopier.onProgress(length);
			}
		} finally {
			streamCopier.onEnd(length);
		}
		return length;
	}

	public static void copy(InputStream input, Writer writer, String charset) throws IOException {
		copy(input, writer, CharsetUtils.toCharset(charset));
	}

	public static void copy(InputStream input, Writer writer, Charset charset) throws IOException {
		copy(input, writer, charset, -1);
	}

	public static void copy(InputStream input, Writer writer, Charset charset, int maxSize) throws IOException {
		copy(input, writer, charset, DEFAULT_BYTE_BUFFER_SIZE, maxSize);
	}

	public static void copy(InputStream input, Writer writer, Charset charset, int bufferSize, int maxSize) throws IOException {
		Assert.isNull(input, "Input stream is null.");
		Reader reader = new InputStreamReader(input, CharsetUtils.toCharset(charset));
		copy(reader, writer, bufferSize, maxSize);
	}

	public static void copy(byte[] input, Writer writer, String charset) throws IOException {
		copy(input, writer, CharsetUtils.toCharset(charset));
	}

	public static void copy(byte[] input, Writer writer, Charset charset) throws IOException {
		copy(input, writer, charset, -1);
	}

	public static void copy(byte[] input, Writer writer, Charset charset, int maxSize) throws IOException {
		copy(input, writer, charset, DEFAULT_BYTE_BUFFER_SIZE, maxSize);
	}

	public static void copy(byte[] input, Writer writer, Charset charset, int bufferSize, int maxSize) throws IOException {
		Assert.isNull(input, "Input must not be null.");
		ByteArrayInputStream in = null;
		try {
			in = new ByteArrayInputStream(input);
			copy(in, writer, charset, bufferSize, maxSize);
		} finally {
			close(in);
		}
	}

	public static void copy(char[] input, OutputStream output, Charset charset) throws IOException {
		Assert.isNull(input, "Input must not be null.");
		CharArrayReader reader = null;
		try {
			reader = new CharArrayReader(input);
			copy(reader, output, charset);
		} finally {
			close(reader);
		}
	}

	public static void copy(char[] input, OutputStream output, String charset) throws IOException {
		copy(input, output, CharsetUtils.toCharset(charset));
	}

	public static void copy(CharSequence input, OutputStream output, Charset charset) throws IOException {
		Assert.isNull(input, "Input must not be null.");
		StringReader reader = null;
		try {
			reader = new StringReader(input.toString());
			copy(reader, output, charset);
		} finally {
			close(reader);
		}
	}

	public static void copy(CharSequence input, OutputStream output, String charset) throws IOException {
		copy(input, output, CharsetUtils.toCharset(charset));
	}

	public static void copy(String[] array, OutputStream output, Charset charset) throws IOException {
		StringBuilder content = StringUtils.toStringBuilder(array);
		copy(content, output, charset);
	}

	public static void copy(String[] array, OutputStream output, String charset) throws IOException {
		copy(array, output, CharsetUtils.toCharset(charset));
	}

	public static void copy(Reader reader, OutputStream output, Charset charset) throws IOException {
		copy(reader, output, charset, -1);
	}

	public static void copy(Reader reader, OutputStream output, Charset charset, int maxSize) throws IOException {
		copy(reader, output, charset, DEFAULT_CHAR_BUFFER_SIZE, maxSize);
	}

	public static void copy(Reader reader, OutputStream output, Charset charset, int bufferSize, int maxSize) throws IOException {
		Assert.isNull(output, "Output must not be null.");
		OutputStreamWriter out = new OutputStreamWriter(output, CharsetUtils.toCharset(charset));
		copy(reader, out, bufferSize, maxSize);
	}

	public static void copy(Reader reader, OutputStream output, String charset) throws IOException {
		copy(reader, output, CharsetUtils.toCharset(charset));
	}

	public static long copy(RandomAccessFile input, OutputStream output) throws IOException {
		return copy(input, output, DEFAULT_BYTE_BUFFER_SIZE, -1);
	}

	public static long copy(RandomAccessFile input, OutputStream output, int maxSize) throws IOException {
		return copy(input, output, DEFAULT_BYTE_BUFFER_SIZE, maxSize);
	}

	public static long copy(final RandomAccessFile raf, final OutputStream output, int bufferSize, int maxSize) throws IOException {
		return copy(raf, new StreamCopier() {
			@Override
			public void copy(byte[] bytes, int offset, int length) throws IOException {
				output.write(bytes, offset, length);
			}
		}, bufferSize, maxSize);
	}

	public static long copy(RandomAccessFile raf, StreamCopier streamCopier, int bufferSize, int maxSize) throws IOException {
		final long filePointer = raf.getFilePointer();
		final boolean capped = maxSize > 0;
		int remaining = maxSize;
		byte[] buffer = getByteBuffer(bufferSize);
		long length = 0;
		try {
			streamCopier.onStart();
			int read;
			while (EOF != (read = raf.read(buffer))) {
				if (capped) {
					if (read > remaining) {
						streamCopier.copy(buffer, 0, remaining);
						break;
					}
					remaining -= read;
				}
				streamCopier.copy(buffer, 0, read);
				length += read;
				streamCopier.onProgress(length);
			}
		} finally {
			raf.seek(filePointer);
			streamCopier.onEnd(length);
		}
		return length;
	}

	public static char[] toCharArray(InputStream input, Charset charset, int bufferSize, int maxSize) throws IOException {
		CharArrayWriter output = null;
		try {
			output = new CharArrayWriter(bufferSize);
			copy(input, output, charset, bufferSize, maxSize);
			return output.toCharArray();
		} finally {
			close(output);
		}
	}

	public static char[] toCharArray(InputStream input, Charset charset, int maxSize) throws IOException {
		return toCharArray(input, charset, DEFAULT_CHAR_BUFFER_SIZE, maxSize);
	}

	public static char[] toCharArray(InputStream input, Charset charset) throws IOException {
		return toCharArray(input, charset, -1);
	}

	public static char[] toCharArray(InputStream is, String charset) throws IOException {
		return toCharArray(is, CharsetUtils.toCharset(charset));
	}

	public static CharBuffer toCharBuffer(InputStream input, Charset charset, int bufferSize, int maxSize) throws IOException {
		char[] chars = toCharArray(input, charset, bufferSize, maxSize);
		return CharBuffer.wrap(chars);
	}

	public static CharBuffer toCharBuffer(InputStream input, Charset charset, int maxSize) throws IOException {
		return toCharBuffer(input, charset, DEFAULT_CHAR_BUFFER_SIZE, maxSize);
	}

	public static CharBuffer toCharBuffer(InputStream input, Charset charset) throws IOException {
		return toCharBuffer(input, charset, -1);
	}

	public static CharBuffer toCharBuffer(InputStream input, String charset) throws IOException {
		return toCharBuffer(input, CharsetUtils.toCharset(charset));
	}

	public static byte[] toByteArray(InputStream input) throws IOException {
		return toByteArray(input, -1);
	}

	public static byte[] toByteArray(InputStream input, int maxSize) throws IOException {
		return toByteArray(input, DEFAULT_BYTE_BUFFER_SIZE, maxSize);
	}

	public static byte[] toByteArray(InputStream input, int bufferSize, int maxSize) throws IOException {
		ByteArrayOutputStream output = null;
		try {
			output = new ByteArrayOutputStream(bufferSize);
			copy(input, output, bufferSize, maxSize);
			return output.toByteArray();
		} finally {
			closeQuietly(output);
		}
	}

	public static byte[] toByteArray(RandomAccessFile input) throws IOException {
		return toByteArray(input, -1);
	}

	public static byte[] toByteArray(RandomAccessFile input, int maxSize) throws IOException {
		return toByteArray(input, DEFAULT_BYTE_BUFFER_SIZE, maxSize);
	}

	public static byte[] toByteArray(RandomAccessFile input, int bufferSize, int maxSize) throws IOException {
		ByteArrayOutputStream output = null;
		try {
			output = new ByteArrayOutputStream(bufferSize);
			copy(input, output, bufferSize, maxSize);
			return output.toByteArray();
		} finally {
			closeQuietly(output);
		}
	}

	public static ByteBuffer toByteBuffer(InputStream input) throws IOException {
		return toByteBuffer(input, -1);
	}

	public static ByteBuffer toByteBuffer(InputStream input, int maxSize) throws IOException {
		return toByteBuffer(input, DEFAULT_BYTE_BUFFER_SIZE, maxSize);
	}

	public static ByteBuffer toByteBuffer(InputStream input, int bufferSize, int maxSize) throws IOException {
		byte[] bytes = toByteArray(input, bufferSize, maxSize);
		return ByteBuffer.wrap(bytes);
	}

	public static ByteBuffer toByteBuffer(RandomAccessFile input) throws IOException {
		return toByteBuffer(input, -1);
	}

	public static ByteBuffer toByteBuffer(RandomAccessFile input, int maxSize) throws IOException {
		return toByteBuffer(input, DEFAULT_BYTE_BUFFER_SIZE, maxSize);
	}

	public static ByteBuffer toByteBuffer(RandomAccessFile input, int bufferSize, int maxSize) throws IOException {
		byte[] bytes = toByteArray(input, bufferSize, maxSize);
		return ByteBuffer.wrap(bytes);
	}

	public static String toString(InputStream is, Charset charset, int maxSize) throws IOException {
		return toString(is, charset, DEFAULT_BYTE_BUFFER_SIZE, maxSize);
	}

	public static String toString(InputStream is, Charset charset, int bufferSize, int maxSize) throws IOException {
		char[] array = toCharArray(is, charset, bufferSize, maxSize);
		return new String(array);
	}

	public static String toString(InputStream is, Charset charset) throws IOException {
		return toString(is, charset, -1);
	}

	public static String toString(InputStream is, String charset) throws IOException {
		return toString(is, CharsetUtils.toCharset(charset));
	}

	public static String toString(Reader reader) throws IOException {
		return toString(reader, -1);
	}

	public static String toString(Reader reader, int maxSize) throws IOException {
		return toString(reader, DEFAULT_CHAR_BUFFER_SIZE, maxSize);
	}

	public static String toString(Reader reader, int bufferSize, int maxSize) throws IOException {
		StringWriter output = null;
		try {
			output = new StringWriter();
			copy(reader, output, bufferSize, maxSize);
			return output.toString();
		} finally {
			close(output);
		}
	}

	public static char[] toCharArray(Reader reader) throws IOException {
		return toCharArray(reader, -1);
	}

	public static char[] toCharArray(Reader reader, int maxSize) throws IOException {
		return toCharArray(reader, DEFAULT_CHAR_BUFFER_SIZE, maxSize);
	}

	public static char[] toCharArray(Reader reader, int bufferSize, int maxSize) throws IOException {
		CharArrayWriter output = null;
		try {
			output = new CharArrayWriter();
			copy(reader, output, bufferSize, maxSize);
			return output.toCharArray();
		} finally {
			close(output);
		}
	}

	public static byte[] toByteArray(Reader reader, String charset) throws IOException {
		return toByteArray(reader, CharsetUtils.toCharset(charset));
	}

	public static byte[] toByteArray(Reader reader, Charset charset) throws IOException {
		return toByteArray(reader, charset, -1);
	}

	public static byte[] toByteArray(Reader reader, Charset charset, int maxSize) throws IOException {
		return toByteArray(reader, charset, DEFAULT_CHAR_BUFFER_SIZE, maxSize);
	}

	public static byte[] toByteArray(Reader reader, Charset charset, int bufferSize, int maxSize) throws IOException {
		ByteArrayOutputStream output = null;
		try {
			output = new ByteArrayOutputStream(bufferSize);
			copy(reader, output, charset, bufferSize, maxSize);
			return output.toByteArray();
		} finally {
			close(output);
		}
	}

	public static int copyLines(Reader reader, StringBuffer str) throws IOException {
		Assert.isNull(reader, "Reader must not be null.");
		Assert.isNull(str, "Output must not bs null.");
		BufferedReader br = null;
		int line = 1;
		try {
			br = getBufferedReader(reader);
			String nextLine;
			while (null != ((nextLine = br.readLine()))) {
				str.append(nextLine);
				str.append(NEWLINE);
				line++;
			}
		} finally {
			close(br);
		}
		return line;
	}

	public static int copyLines(InputStream in, Charset charset, StringBuffer str) throws IOException {
		return copyLines(new InputStreamReader(in, CharsetUtils.toCharset(charset)), str);
	}

	public static int copyLines(InputStream in, String charset, StringBuffer str) throws IOException {
		return copyLines(in, CharsetUtils.toCharset(charset), str);
	}

	public static int copyLines(InputStream in, Charset charset, StringBuilder str) throws IOException {
		return copyLines(new InputStreamReader(in, CharsetUtils.toCharset(charset)), str);
	}

	public static int copyLines(InputStream in, String charset, StringBuilder str) throws IOException {
		return copyLines(in, CharsetUtils.toCharset(charset), str);
	}

	public static int copyLines(Reader reader, StringBuilder str) throws IOException {
		Assert.isNull(reader, "Reader must not be null.");
		Assert.isNull(str, "Output must not bs null.");
		BufferedReader br = null;
		int line = 1;
		try {
			br = getBufferedReader(reader);
			String nextLine;

			while (null != ((nextLine = br.readLine()))) {
				str.append(nextLine);
				str.append(NEWLINE);
				line++;
			}
		} finally {
			close(br);
		}
		return line;
	}

	public static void copyLines(Reader reader, List<String> results) throws IOException {
		Assert.isNull(reader, "Reader must not be null.");
		Assert.isNull(results, "Output must not bs null.");
		BufferedReader br = null;
		try {
			br = getBufferedReader(reader);
			String nextLine;
			while (null != (nextLine = br.readLine())) {
				results.add(nextLine);
			}
		} finally {
			close(br);
		}
	}

	public static void copyLines(InputStream in, Charset charset, List<String> results) throws IOException {
		Assert.isNull(in, "Input stream must not be null.");
		copyLines(new InputStreamReader(in, CharsetUtils.toCharset(charset)), results);
	}

	public static void copyLines(InputStream in, String charset, List<String> results) throws IOException {
		copyLines(in, CharsetUtils.toCharset(charset), results);
	}

	public static void copyLines(byte[] bytes, Charset charset, List<String> results) throws IOException {
		Assert.isNull(bytes, "Input must not be null.");
		InputStream in = null;
		try {
			in = new ByteArrayInputStream(bytes);
			copyLines(in, charset, results);
		} finally {
			close(in);
		}
	}

	public static void copyLines(byte[] bytes, String charset, List<String> results) throws IOException {
		copyLines(bytes, CharsetUtils.toCharset(charset), results);
	}

	public static void copyLines(char[] array, List<String> results) throws IOException {
		Reader reader = null;
		try {
			reader = new CharArrayReader(array);
			copyLines(reader, results);
		} finally {
			close(reader);
		}
	}

	public static void writeLines(Map<String, String> results, Writer writer) throws IOException {
		writeLines(results, writer, "=");
	}

	public static void writeLines(Map<String, String> results, Writer writer, String separator) throws IOException {
		BufferedWriter bw = getBufferedWriter(writer);
		try {
			for (Map.Entry<String, String> en : results.entrySet()) {
				bw.write(en.getKey() + separator + en.getValue());
				bw.newLine();
			}
		} finally {
			flushQuietly(bw);
		}
	}

	public static void writeLines(Map<String, String> results, OutputStream os, String charset) throws IOException {
		writeLines(results, os, CharsetUtils.toCharset(charset));
	}

	public static void writeLines(Map<String, String> results, OutputStream os, Charset charset) throws IOException {
		writeLines(results, os, charset, "=");
	}

	public static void writeLines(Map<String, String> results, OutputStream os, Charset charset, String separator) throws IOException {
		BufferedWriter bw = getBufferedWriter(os, charset);
		try {
			for (Map.Entry<String, String> en : results.entrySet()) {
				bw.write(en.getKey() + separator + en.getValue());
				bw.newLine();
			}
		} finally {
			flushQuietly(bw);
		}
	}

	public static void writeLines(Enumeration<String> results, Writer writer) throws IOException {
		BufferedWriter bw = getBufferedWriter(writer);
		try {
			while (results.hasMoreElements()) {
				bw.write(results.nextElement());
				bw.newLine();
			}
		} finally {
			flushQuietly(bw);
		}
	}

	public static void writeLines(Enumeration<String> results, OutputStream os, String charset) throws IOException {
		writeLines(results, os, CharsetUtils.toCharset(charset));
	}

	public static void writeLines(Enumeration<String> results, OutputStream os, Charset charset) throws IOException {
		BufferedWriter bw = getBufferedWriter(os, charset);
		try {
			while (results.hasMoreElements()) {
				bw.write(results.nextElement());
				bw.newLine();
			}
		} finally {
			flushQuietly(bw);
		}
	}

	public static void writeLines(Iterator<String> results, Writer writer) throws IOException {
		BufferedWriter bw = getBufferedWriter(writer);
		try {
			while (results.hasNext()) {
				bw.write(results.next());
				bw.newLine();
			}
		} finally {
			flushQuietly(bw);
		}
	}

	public static void writeLines(Iterator<String> results, OutputStream os, String charset) throws IOException {
		writeLines(results, os, CharsetUtils.toCharset(charset));
	}

	public static void writeLines(Iterator<String> results, OutputStream os, Charset charset) throws IOException {
		BufferedWriter bw = getBufferedWriter(os, charset);
		try {
			while (results.hasNext()) {
				bw.write(results.next());
				bw.newLine();
			}
		} finally {
			flushQuietly(bw);
		}
	}

	public static void writeLines(Collection<String> results, Writer writer) throws IOException {
		writeLines(results.iterator(), writer);
	}

	public static void writeLines(Collection<String> results, OutputStream os, String charset) throws IOException {
		writeLines(results, os, CharsetUtils.toCharset(charset));
	}

	public static void writeLines(Collection<String> results, OutputStream os, Charset charset) throws IOException {
		writeLines(results.iterator(), os, charset);
	}

	public static void checkRange(int start, int limit) throws IOException {
		boolean result = (start > 0 && limit < 0) || ((start > 0 && limit > 0) && (start < start + limit));
		if (!result) {
			throw new IOException("Invalid range input: " + start + ", " + limit);
		}
	}

	static boolean isValidLine(int start, int limit, int lineNumber) {
		return (limit < 0) || (lineNumber < start + limit - 1);
	}

	public static void rangeCopyLines(LineNumberReader reader, int start, int limit, List<String> results) throws IOException {
		Assert.isNull(reader, "Reader must not be null.");
		Assert.isNull(results, "Output must not be null.");
		checkRange(start, limit);
		String nextLine;
		reader.mark(start);
		int n;
		while (null != ((nextLine = reader.readLine()))) {
			n = reader.getLineNumber();
			if (n < start) {
				continue;
			}
			results.add(nextLine);
			if (!isValidLine(start, limit, n)) {
				break;
			}
		}
	}

	public static void rangeCopyLines(LineNumberReader reader, int start, int limit, StringBuffer str) throws IOException {
		Assert.isNull(reader, "Reader must not be null.");
		Assert.isNull(str, "Output must not be null.");
		checkRange(start, limit);
		String nextLine;
		reader.mark(start);
		int n;
		while (null != ((nextLine = reader.readLine()))) {
			n = reader.getLineNumber();
			if (n < start) {
				continue;
			}
			str.append(nextLine);
			if (isValidLine(start, limit, n)) {
				str.append(NEWLINE);
			} else {
				break;
			}
		}

	}

	public static void rangeCopyLines(LineNumberReader reader, int start, int limit, StringBuilder str) throws IOException {
		Assert.isNull(reader, "Reader must not be null.");
		Assert.isNull(str, "Output must not be null.");
		checkRange(start, limit);
		String nextLine;
		reader.mark(start);
		int n;
		while (null != ((nextLine = reader.readLine()))) {
			n = reader.getLineNumber();
			if (n < start) {
				continue;
			}
			str.append(nextLine);
			if (isValidLine(start, limit, n)) {
				str.append(NEWLINE);
			} else {
				break;
			}
		}
	}

	public static BufferedInputStream getBufferedInputStream(InputStream in) {
		return getBufferedInputStream(in, DEFAULT_BYTE_BUFFER_SIZE);
	}

	public static BufferedOutputStream getBufferedOutputStream(OutputStream out) {
		return getBufferedOutputStream(out, DEFAULT_BYTE_BUFFER_SIZE);
	}

	public static BufferedInputStream getBufferedInputStream(InputStream in, int size) {
		Assert.isNull(in, "Input stream must not be null.");
		return in instanceof BufferedInputStream ? (BufferedInputStream) in : new BufferedInputStream(in, size);
	}

	public static BufferedOutputStream getBufferedOutputStream(OutputStream out, int size) {
		Assert.isNull(out, "Output stream must not be null.");
		return out instanceof BufferedOutputStream ? (BufferedOutputStream) out : new BufferedOutputStream(out, size);
	}

	public static BufferedReader getBufferedReader(Reader reader) {
		return getBufferedReader(reader, DEFAULT_CHAR_BUFFER_SIZE);
	}

	public static LineNumberReader getLineNumberReader(Reader reader) {
		return getLineNumberReader(reader, DEFAULT_CHAR_BUFFER_SIZE);
	}

	public static BufferedWriter getBufferedWriter(Writer writer) {
		return getBufferedWriter(writer, DEFAULT_CHAR_BUFFER_SIZE);
	}

	public static BufferedReader getBufferedReader(Reader reader, int size) {
		Assert.isNull(reader, "Reader must not be null.");
		return reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader, size);
	}

	public static LineNumberReader getLineNumberReader(Reader reader, int size) {
		Assert.isNull(reader, "Reader must not be null.");
		return reader instanceof LineNumberReader ? (LineNumberReader) reader : new LineNumberReader(reader, size);
	}

	public static BufferedWriter getBufferedWriter(Writer writer, int size) {
		Assert.isNull(writer, "Writer must not be null.");
		return writer instanceof BufferedWriter ? (BufferedWriter) writer : new BufferedWriter(writer, size);
	}

	public static BufferedReader getBufferedReader(InputStream in, Charset charset) {
		return getBufferedReader(in, charset, DEFAULT_CHAR_BUFFER_SIZE);
	}

	public static LineNumberReader getLineNumberReader(InputStream in, Charset charset, int size) {
		Assert.isNull(in, "Input stream must not be null.");
		return getLineNumberReader(new InputStreamReader(in, CharsetUtils.toCharset(charset)), size);
	}

	public static LineNumberReader getLineNumberReader(InputStream in, Charset charset) {
		return getLineNumberReader(in, charset, DEFAULT_CHAR_BUFFER_SIZE);
	}

	public static LineNumberReader getLineNumberReader(InputStream in, String charset, int size) {
		return getLineNumberReader(in, CharsetUtils.toCharset(charset), size);
	}

	public static LineNumberReader getLineNumberReader(InputStream in, String charset) {
		return getLineNumberReader(in, charset, DEFAULT_CHAR_BUFFER_SIZE);
	}

	public static BufferedReader getBufferedReader(InputStream in, String charset) {
		return getBufferedReader(in, CharsetUtils.toCharset(charset));
	}

	public static BufferedReader getBufferedReader(InputStream in, Charset charset, int size) {
		Assert.isNull(in, "Input stream must not be null.");
		return getBufferedReader(new InputStreamReader(in, CharsetUtils.toCharset(charset)), size);
	}

	public static BufferedReader getBufferedReader(InputStream in, String charset, int size) {
		return getBufferedReader(in, CharsetUtils.toCharset(charset), size);
	}

	public static BufferedWriter getBufferedWriter(OutputStream os, Charset charset) {
		return getBufferedWriter(os, charset, DEFAULT_CHAR_BUFFER_SIZE);
	}

	public static BufferedWriter getBufferedWriter(OutputStream os, String charset) {
		return getBufferedWriter(os, CharsetUtils.toCharset(charset));
	}

	public static BufferedWriter getBufferedWriter(OutputStream os, Charset charset, int size) {
		return getBufferedWriter(new OutputStreamWriter(os, CharsetUtils.toCharset(charset)), size);
	}

	public static BufferedWriter getBufferedWriter(OutputStream os, String charset, int size) {
		return getBufferedWriter(os, CharsetUtils.toCharset(charset), size);
	}

	public static ObjectInputStream getObjectInputStream(InputStream in) throws IOException {
		return in instanceof ObjectInputStream ? (ObjectInputStream) in : new ObjectInputStream(in);
	}

	public static ObjectOutputStream getObjectOutputStream(OutputStream out) throws IOException {
		return out instanceof ObjectOutputStream ? (ObjectOutputStream) out : new ObjectOutputStream(out);
	}

	public static DataInputStream getDataInputStream(InputStream in) {
		return in instanceof DataInputStream ? (DataInputStream) in : new DataInputStream(in);
	}

	public static DataOutputStream getDataOutputStream(OutputStream out) {
		return out instanceof DataOutputStream ? (DataOutputStream) out : new DataOutputStream(out);
	}

	public static GZIPInputStream getGZIPInputStream(InputStream in) throws IOException {
		return getGZIPInputStream(in, 1024);
	}

	public static GZIPInputStream getGZIPInputStream(InputStream in, int size) throws IOException {
		return in instanceof GZIPInputStream ? (GZIPInputStream) in : new GZIPInputStream(in, size);
	}

	public static GZIPOutputStream getGZIPOutputStream(OutputStream out) throws IOException {
		return getGZIPOutputStream(out, 1024);
	}

	public static GZIPOutputStream getGZIPOutputStream(OutputStream out, int size) throws IOException {
		return out instanceof GZIPOutputStream ? (GZIPOutputStream) out : new GZIPOutputStream(out, size);
	}

	public static ByteBuffer emptyByteBuffer() {
		return ByteBuffer.allocate(0);
	}

	public static InputStream emptyInputStream() {
		return new ByteArrayInputStream(new byte[0]);
	}

}
