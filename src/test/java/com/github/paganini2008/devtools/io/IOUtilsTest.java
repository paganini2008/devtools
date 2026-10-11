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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.github.paganini2008.devtools.io.IoTestSupport.FailingInputStream;
import com.github.paganini2008.devtools.io.IoTestSupport.FailingOutputStream;
import com.github.paganini2008.devtools.io.IoTestSupport.TrackingInputStream;
import com.github.paganini2008.devtools.io.IoTestSupport.TrackingOutputStream;
import com.github.paganini2008.devtools.io.IoTestSupport.TrackingReader;
import com.github.paganini2008.devtools.io.IoTestSupport.TrackingWriter;

/**
 * IOUtilsTest
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class IOUtilsTest {

	private static final String NL = IOUtils.NEWLINE;
	private static final Charset GBK = Charset.forName("GBK");
	private static final String CHINESE = "中文编码测试";

	@TempDir
	File tempDir;

	private static byte[] randomBytes(int size) {
		byte[] data = new byte[size];
		new Random(42).nextBytes(data);
		return data;
	}

	// ---------------------------------------------------------------- write

	@Test
	public void write_bytesWritesContentAndIgnoresNull() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		IOUtils.write(new byte[] { 1, 2, 3 }, out);
		IOUtils.write((byte[]) null, out);
		assertArrayEquals(new byte[] { 1, 2, 3 }, out.toByteArray());
		assertThrows(IllegalArgumentException.class, () -> IOUtils.write(new byte[1], null));
	}

	@Test
	public void write_charsWritesContentAndIgnoresNull() throws IOException {
		StringWriter writer = new StringWriter();
		IOUtils.write("abc".toCharArray(), writer);
		IOUtils.write((char[]) null, writer);
		assertEquals("abc", writer.toString());
		assertThrows(IllegalArgumentException.class, () -> IOUtils.write("a".toCharArray(), null));
	}

	@Test
	public void write_charSequenceSkipsNullAndBlankContent() throws IOException {
		StringWriter writer = new StringWriter();
		IOUtils.write("abc", writer);
		IOUtils.write((CharSequence) null, writer);
		IOUtils.write("   ", writer);
		IOUtils.write(new StringBuilder(CHINESE), writer);
		assertEquals("abc" + CHINESE, writer.toString());
		assertThrows(IllegalArgumentException.class, () -> IOUtils.write("a", (StringWriter) null));
	}

	@Test
	public void writeLine_appendsNewlineOnlyForNonBlankContent() throws IOException {
		StringWriter writer = new StringWriter();
		IOUtils.writeLine("a", writer);
		IOUtils.writeLine("", writer);
		IOUtils.writeLine(null, writer);
		IOUtils.writeLine("b", writer);
		assertEquals("a" + NL + "b" + NL, writer.toString());
		assertThrows(IllegalArgumentException.class, () -> IOUtils.writeLine("a", null));
	}

	// ---------------------------------------------------------------- close / flush

	@Test
	public void close_closesAndIgnoresNull() throws IOException {
		TrackingInputStream in = new TrackingInputStream(new byte[0]);
		IOUtils.close(in);
		IOUtils.close(null);
		assertEquals(1, in.closeCount);
		assertThrows(IOException.class, () -> IOUtils.close(new FailingInputStream()));
	}

	@Test
	public void closeQuietly_swallowsExceptions() {
		TrackingInputStream in = new TrackingInputStream(new byte[0]);
		IOUtils.closeQuietly(in);
		IOUtils.closeQuietly(null);
		IOUtils.closeQuietly(new FailingInputStream());
		assertEquals(1, in.closeCount);
	}

	@Test
	public void flush_flushesStreamsAndWriters() throws IOException {
		TrackingOutputStream out = new TrackingOutputStream();
		TrackingWriter writer = new TrackingWriter();
		IOUtils.flush(out);
		IOUtils.flush(writer);
		IOUtils.flush((java.io.OutputStream) null);
		IOUtils.flush((java.io.Writer) null);
		IOUtils.flushQuietly(out);
		IOUtils.flushQuietly(writer);
		IOUtils.flushQuietly((java.io.OutputStream) null);
		IOUtils.flushQuietly((java.io.Writer) null);
		IOUtils.flushQuietly(new FailingOutputStream());
		IOUtils.flushQuietly(new java.io.OutputStreamWriter(new FailingOutputStream()));
		assertEquals(2, out.flushCount);
		assertEquals(2, writer.flushCount);
		assertThrows(IOException.class, () -> IOUtils.flush(new FailingOutputStream()));
	}

	@Test
	public void flushAndCloseQuietly_flushesThenClosesWithoutThrowing() {
		TrackingOutputStream out = new TrackingOutputStream();
		TrackingWriter writer = new TrackingWriter();
		IOUtils.flushAndCloseQuietly(out);
		IOUtils.flushAndCloseQuietly(writer);
		IOUtils.flushAndCloseQuietly(new FailingOutputStream());
		IOUtils.flushAndCloseQuietly((java.io.Writer) null);
		assertTrue(out.isClosed());
		assertTrue(out.flushCount >= 1);
		assertEquals(1, writer.closeCount);
		assertTrue(writer.flushCount >= 1);
	}

	@Test
	public void flushAndClose_propagatesFlushErrorButStillCloses() throws IOException {
		TrackingOutputStream out = new TrackingOutputStream();
		IOUtils.flushAndClose(out);
		assertTrue(out.isClosed());
		TrackingWriter writer = new TrackingWriter();
		IOUtils.flushAndClose(writer);
		assertEquals(1, writer.closeCount);

		final int[] closed = new int[1];
		FailingOutputStream failing = new FailingOutputStream() {
			@Override
			public void close() {
				closed[0]++;
			}
		};
		IOException e = assertThrows(IOException.class, () -> IOUtils.flushAndClose(failing));
		assertEquals("flush failed", e.getMessage());
		assertEquals(1, closed[0]);
		IOUtils.flushAndClose((java.io.Writer) null);
		IOUtils.flushAndClose((java.io.OutputStream) null);
		assertThrows(IOException.class, () -> IOUtils.flushAndClose(new java.io.OutputStreamWriter(new FailingOutputStream()) {
			@Override
			public void flush() throws IOException {
				throw new IOException("writer flush failed");
			}

			@Override
			public void close() {
			}
		}));
	}

	// ---------------------------------------------------------------- copy reader/writer

	@Test
	public void copyLines_readerToWriterNormalizesLineSeparatorsAndKeepsReaderOpen() throws IOException {
		TrackingReader reader = new TrackingReader("a\r\nb\nc");
		TrackingWriter writer = new TrackingWriter();
		IOUtils.copyLines(reader, writer);
		assertEquals("a" + NL + "b" + NL + "c" + NL, writer.toString());
		assertEquals(0, reader.closeCount);
		assertEquals(0, writer.closeCount);
		assertTrue(writer.flushCount > 0);
		assertThrows(IllegalArgumentException.class, () -> IOUtils.copyLines((java.io.Reader) null, new StringWriter()));
		assertThrows(IllegalArgumentException.class, () -> IOUtils.copyLines(new StringReader(""), (StringWriter) null));
	}

	@Test
	public void copy_readerToWriterReturnsCharCount() throws IOException {
		TrackingReader reader = new TrackingReader(CHINESE + "abc");
		StringWriter writer = new StringWriter();
		assertEquals(CHINESE.length() + 3, IOUtils.copy(reader, writer));
		assertEquals(CHINESE + "abc", writer.toString());
		assertEquals(0, reader.closeCount);
		assertEquals(0, IOUtils.copy(new StringReader(""), new StringWriter()));
	}

	@Test
	public void copy_readerToWriterHonorsMaxSizeAndReturnsCopiedCount() throws IOException {
		StringWriter writer = new StringWriter();
		assertEquals(4, IOUtils.copy(new StringReader("abcdefghij"), writer, 3, 4));
		assertEquals("abcd", writer.toString());

		writer = new StringWriter();
		assertEquals(6, IOUtils.copy(new StringReader("abcdef"), writer, 3, 6));
		assertEquals("abcdef", writer.toString());

		writer = new StringWriter();
		assertEquals(3, IOUtils.copy(new StringReader("abc"), writer, 100));
		assertEquals("abc", writer.toString());

		// non positive buffer size falls back to default
		writer = new StringWriter();
		assertEquals(3, IOUtils.copy(new StringReader("abc"), writer, 0, -1));
		assertEquals("abc", writer.toString());
	}

	@Test
	public void copy_readerToWriterRejectsNullArguments() {
		assertThrows(IllegalArgumentException.class, () -> IOUtils.copy((java.io.Reader) null, new StringWriter()));
		assertThrows(IllegalArgumentException.class, () -> IOUtils.copy(new StringReader(""), (java.io.Writer) null));
	}

	// ---------------------------------------------------------------- copy streams

	@Test
	public void copy_streamToStreamCopiesEverythingWithoutClosing() throws IOException {
		byte[] data = randomBytes(20000);
		TrackingInputStream in = new TrackingInputStream(data);
		TrackingOutputStream out = new TrackingOutputStream();
		assertEquals(data.length, IOUtils.copy(in, out));
		assertArrayEquals(data, out.toByteArray());
		assertFalse(in.isClosed());
		assertFalse(out.isClosed());
	}

	@Test
	public void copy_streamToStreamHonorsMaxSize() throws IOException {
		byte[] data = randomBytes(100);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		assertEquals(10, IOUtils.copy(new ByteArrayInputStream(data), out, 10));
		assertArrayEquals(Arrays.copyOf(data, 10), out.toByteArray());

		out = new ByteArrayOutputStream();
		assertEquals(25, IOUtils.copy(new ByteArrayInputStream(data), out, 7, 25));
		assertArrayEquals(Arrays.copyOf(data, 25), out.toByteArray());

		out = new ByteArrayOutputStream();
		assertEquals(100, IOUtils.copy(new ByteArrayInputStream(data), out, -1, 1000));
		assertArrayEquals(data, out.toByteArray());
	}

	@Test
	public void copy_largeStreamIsCopiedCompletely() throws IOException {
		byte[] data = randomBytes(3 * 1024 * 1024 + 17);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		assertEquals(data.length, IOUtils.copy(new ByteArrayInputStream(data), out));
		assertArrayEquals(data, out.toByteArray());
	}

	@Test
	public void copy_streamCopierReceivesLifecycleCallbacks() throws IOException {
		final List<String> events = new ArrayList<String>();
		final AtomicLong progress = new AtomicLong();
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		long length = IOUtils.copy(new ByteArrayInputStream(randomBytes(10)), new StreamCopier() {

			@Override
			public void onStart() {
				events.add("start");
			}

			@Override
			public void copy(byte[] bytes, int offset, int length) {
				out.write(bytes, offset, length);
			}

			@Override
			public void onProgress(long length) {
				progress.set(length);
			}

			@Override
			public void onEnd(long length) {
				events.add("end:" + length);
			}
		}, 4, 9);
		assertEquals(9, length);
		assertEquals(9, progress.get());
		assertEquals(9, out.size());
		assertEquals(Arrays.asList("start", "end:9"), events);
	}

	@Test
	public void copy_streamCopierDefaultsAndOnEndCalledOnFailure() throws IOException {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		assertEquals(3, IOUtils.copy(new ByteArrayInputStream(new byte[3]), (bytes, offset, len) -> out.write(bytes, offset, len), 0, -1));
		final long[] end = { -1 };
		assertThrows(IOException.class, () -> IOUtils.copy(new FailingInputStream(), new StreamCopier() {
			@Override
			public void copy(byte[] bytes, int offset, int length) {
			}

			@Override
			public void onEnd(long length) {
				end[0] = length;
			}
		}, 0, -1));
		assertEquals(0, end[0]);
		assertThrows(IllegalArgumentException.class, () -> IOUtils.copy((InputStream) null, (b, o, l) -> {
		}, 0, 0));
		assertThrows(IllegalArgumentException.class, () -> IOUtils.copy(new ByteArrayInputStream(new byte[0]), (StreamCopier) null, 0, 0));
	}

	@Test
	public void copy_streamToStreamPropagatesWriteFailure() {
		IOException e = assertThrows(IOException.class, () -> IOUtils.copy(new ByteArrayInputStream(new byte[3]), new FailingOutputStream()));
		assertEquals("write failed", e.getMessage());
	}

	// ---------------------------------------------------------------- copy with charset

	@Test
	public void copy_streamToWriterDecodesUtf8AndGbk() throws IOException {
		StringWriter writer = new StringWriter();
		IOUtils.copy(new ByteArrayInputStream(CHINESE.getBytes(StandardCharsets.UTF_8)), writer, "UTF-8");
		assertEquals(CHINESE, writer.toString());

		writer = new StringWriter();
		IOUtils.copy(new ByteArrayInputStream(CHINESE.getBytes(GBK)), writer, GBK);
		assertEquals(CHINESE, writer.toString());

		writer = new StringWriter();
		IOUtils.copy(new ByteArrayInputStream(CHINESE.getBytes(GBK)), writer, GBK, 2);
		assertEquals(CHINESE.substring(0, 2), writer.toString());
		assertThrows(IllegalArgumentException.class, () -> IOUtils.copy((InputStream) null, new StringWriter(), GBK));
	}

	@Test
	public void copy_bytesToWriterDecodesWithCharset() throws IOException {
		StringWriter writer = new StringWriter();
		IOUtils.copy(CHINESE.getBytes(GBK), writer, "GBK");
		assertEquals(CHINESE, writer.toString());

		writer = new StringWriter();
		IOUtils.copy(CHINESE.getBytes(StandardCharsets.UTF_8), writer, StandardCharsets.UTF_8, 3);
		assertEquals(CHINESE.substring(0, 3), writer.toString());
		assertThrows(IllegalArgumentException.class, () -> IOUtils.copy((byte[]) null, new StringWriter(), GBK));
	}

	@Test
	public void copy_charsAndCharSequenceToStreamEncodeWithCharset() throws IOException {
		TrackingOutputStream out = new TrackingOutputStream();
		IOUtils.copy(CHINESE.toCharArray(), out, "GBK");
		assertArrayEquals(CHINESE.getBytes(GBK), out.toByteArray());
		assertFalse(out.isClosed());

		out = new TrackingOutputStream();
		IOUtils.copy(new StringBuilder(CHINESE), out, "UTF-8");
		assertArrayEquals(CHINESE.getBytes(StandardCharsets.UTF_8), out.toByteArray());

		out = new TrackingOutputStream();
		IOUtils.copy(new String[] { "a", "中", "c" }, out, "UTF-8");
		assertEquals("a中c", new String(out.toByteArray(), StandardCharsets.UTF_8));

		assertThrows(IllegalArgumentException.class, () -> IOUtils.copy((char[]) null, new ByteArrayOutputStream(), GBK));
		assertThrows(IllegalArgumentException.class, () -> IOUtils.copy((CharSequence) null, new ByteArrayOutputStream(), GBK));
	}

	@Test
	public void copy_readerToStreamEncodesAndHonorsMaxSize() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		IOUtils.copy(new StringReader(CHINESE), out, "GBK");
		assertArrayEquals(CHINESE.getBytes(GBK), out.toByteArray());

		out = new ByteArrayOutputStream();
		IOUtils.copy(new StringReader(CHINESE), out, StandardCharsets.UTF_8, 2);
		assertEquals(CHINESE.substring(0, 2), new String(out.toByteArray(), StandardCharsets.UTF_8));
		assertThrows(IllegalArgumentException.class, () -> IOUtils.copy(new StringReader(""), null, GBK));
	}

	// ---------------------------------------------------------------- random access file

	@Test
	public void copy_randomAccessFileCopiesFromPointerAndRestoresIt() throws IOException {
		File file = new File(tempDir, "raf.bin");
		byte[] data = randomBytes(50);
		Files.write(file.toPath(), data);
		try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
			raf.seek(10);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			assertEquals(40, IOUtils.copy(raf, out));
			assertArrayEquals(Arrays.copyOfRange(data, 10, 50), out.toByteArray());
			assertEquals(10, raf.getFilePointer());

			out = new ByteArrayOutputStream();
			assertEquals(5, IOUtils.copy(raf, out, 5));
			assertArrayEquals(Arrays.copyOfRange(data, 10, 15), out.toByteArray());
			assertEquals(10, raf.getFilePointer());

			assertArrayEquals(Arrays.copyOfRange(data, 10, 50), IOUtils.toByteArray(raf));
			assertArrayEquals(Arrays.copyOfRange(data, 10, 13), IOUtils.toByteArray(raf, 3));
			assertEquals(ByteBuffer.wrap(Arrays.copyOfRange(data, 10, 50)), IOUtils.toByteBuffer(raf));
			assertEquals(ByteBuffer.wrap(Arrays.copyOfRange(data, 10, 12)), IOUtils.toByteBuffer(raf, 2));
			assertEquals(10, raf.getFilePointer());
		}
		assertThrows(IllegalArgumentException.class, () -> IOUtils.copy((RandomAccessFile) null, new ByteArrayOutputStream()));
	}

	@Test
	public void copy_randomAccessFileWithCopierReportsProgress() throws IOException {
		File file = new File(tempDir, "raf2.bin");
		Files.write(file.toPath(), randomBytes(30));
		final long[] progress = new long[1];
		try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
			long n = IOUtils.copy(raf, new StreamCopier() {
				@Override
				public void copy(byte[] bytes, int offset, int length) {
				}

				@Override
				public void onProgress(long length) {
					progress[0] = length;
				}
			}, 8, -1);
			assertEquals(30, n);
			assertEquals(30, progress[0]);
		}
	}

	// ---------------------------------------------------------------- toXxx

	@Test
	public void toCharArray_fromStreamDecodesWithCharset() throws IOException {
		byte[] gbk = CHINESE.getBytes(GBK);
		assertArrayEquals(CHINESE.toCharArray(), IOUtils.toCharArray(new ByteArrayInputStream(gbk), "GBK"));
		assertArrayEquals(CHINESE.substring(0, 1).toCharArray(), IOUtils.toCharArray(new ByteArrayInputStream(gbk), GBK, 1));
		assertArrayEquals(CHINESE.toCharArray(), IOUtils.toCharArray(new ByteArrayInputStream(gbk), GBK, -1, -1));
		assertArrayEquals(new char[0], IOUtils.toCharArray(IOUtils.emptyInputStream(), GBK));
	}

	@Test
	public void toCharBuffer_fromStream() throws IOException {
		byte[] utf8 = CHINESE.getBytes(StandardCharsets.UTF_8);
		assertEquals(CHINESE, IOUtils.toCharBuffer(new ByteArrayInputStream(utf8), "UTF-8").toString());
		assertEquals(CHINESE.substring(0, 2), IOUtils.toCharBuffer(new ByteArrayInputStream(utf8), StandardCharsets.UTF_8, 2).toString());
		CharBuffer buffer = IOUtils.toCharBuffer(new ByteArrayInputStream(utf8), StandardCharsets.UTF_8, 0, -1);
		assertEquals(CHINESE.length(), buffer.remaining());
	}

	@Test
	public void toByteArray_fromStreamWithAndWithoutLimit() throws IOException {
		byte[] data = randomBytes(1000);
		TrackingInputStream in = new TrackingInputStream(data);
		assertArrayEquals(data, IOUtils.toByteArray(in));
		assertFalse(in.isClosed());
		assertArrayEquals(Arrays.copyOf(data, 10), IOUtils.toByteArray(new ByteArrayInputStream(data), 10));
		assertArrayEquals(data, IOUtils.toByteArray(new ByteArrayInputStream(data), 0, -1));
		assertArrayEquals(new byte[0], IOUtils.toByteArray(IOUtils.emptyInputStream()));
	}

	@Test
	public void toByteBuffer_fromStream() throws IOException {
		byte[] data = randomBytes(64);
		assertEquals(ByteBuffer.wrap(data), IOUtils.toByteBuffer(new ByteArrayInputStream(data)));
		assertEquals(ByteBuffer.wrap(Arrays.copyOf(data, 4)), IOUtils.toByteBuffer(new ByteArrayInputStream(data), 4));
	}

	@Test
	public void toString_fromStreamAndReader() throws IOException {
		byte[] gbk = CHINESE.getBytes(GBK);
		assertEquals(CHINESE, IOUtils.toString(new ByteArrayInputStream(gbk), "GBK"));
		assertEquals(CHINESE, IOUtils.toString(new ByteArrayInputStream(gbk), GBK));
		assertEquals(CHINESE.substring(0, 3), IOUtils.toString(new ByteArrayInputStream(gbk), GBK, 3));
		assertEquals("", IOUtils.toString(IOUtils.emptyInputStream(), GBK));

		TrackingReader reader = new TrackingReader("a\nb");
		assertEquals("a\nb", IOUtils.toString(reader));
		assertEquals(0, reader.closeCount);
		assertEquals("ab", IOUtils.toString(new StringReader("abc"), 2));
	}

	@Test
	public void toCharArray_fromReader() throws IOException {
		assertArrayEquals("abc".toCharArray(), IOUtils.toCharArray(new StringReader("abc")));
		assertArrayEquals("a".toCharArray(), IOUtils.toCharArray(new StringReader("abc"), 1));
	}

	@Test
	public void toByteArray_fromReaderEncodesWithCharset() throws IOException {
		assertArrayEquals(CHINESE.getBytes(GBK), IOUtils.toByteArray(new StringReader(CHINESE), "GBK"));
		assertArrayEquals(CHINESE.substring(0, 1).getBytes(StandardCharsets.UTF_8),
				IOUtils.toByteArray(new StringReader(CHINESE), StandardCharsets.UTF_8, 1));
		assertArrayEquals("ab".getBytes(StandardCharsets.UTF_8), IOUtils.toByteArray(new StringReader("ab"), StandardCharsets.UTF_8, -5, -1));
	}

	// ---------------------------------------------------------------- copyLines

	@Test
	public void copyLines_toStringBufferReturnsLineCountPlusOneAndClosesReader() throws IOException {
		TrackingReader reader = new TrackingReader("a\nb\r\nc");
		StringBuffer buffer = new StringBuffer();
		assertEquals(4, IOUtils.copyLines(reader, buffer));
		assertEquals("a" + NL + "b" + NL + "c" + NL, buffer.toString());
		assertEquals(1, reader.closeCount);
		assertEquals(1, IOUtils.copyLines(new StringReader(""), new StringBuffer()));
		assertThrows(IllegalArgumentException.class, () -> IOUtils.copyLines((java.io.Reader) null, new StringBuffer()));
		assertThrows(IllegalArgumentException.class, () -> IOUtils.copyLines(new StringReader(""), (StringBuffer) null));
	}

	@Test
	public void copyLines_toStringBuilderReturnsLineCountPlusOneAndClosesReader() throws IOException {
		TrackingReader reader = new TrackingReader("x\ny");
		StringBuilder builder = new StringBuilder();
		assertEquals(3, IOUtils.copyLines(reader, builder));
		assertEquals("x" + NL + "y" + NL, builder.toString());
		assertEquals(1, reader.closeCount);
		assertThrows(IllegalArgumentException.class, () -> IOUtils.copyLines((java.io.Reader) null, new StringBuilder()));
		assertThrows(IllegalArgumentException.class, () -> IOUtils.copyLines(new StringReader(""), (StringBuilder) null));
	}

	@Test
	public void copyLines_fromStreamWithCharset() throws IOException {
		byte[] gbk = ("第一行\n第二行").getBytes(GBK);
		StringBuffer buffer = new StringBuffer();
		assertEquals(3, IOUtils.copyLines(new ByteArrayInputStream(gbk), "GBK", buffer));
		assertEquals("第一行" + NL + "第二行" + NL, buffer.toString());
		StringBuilder builder = new StringBuilder();
		assertEquals(3, IOUtils.copyLines(new ByteArrayInputStream(gbk), "GBK", builder));
		assertEquals(buffer.toString(), builder.toString());

		TrackingInputStream in = new TrackingInputStream(gbk);
		List<String> lines = new ArrayList<String>();
		IOUtils.copyLines(in, "GBK", lines);
		assertEquals(Arrays.asList("第一行", "第二行"), lines);
		assertTrue(in.isClosed());
		assertThrows(IllegalArgumentException.class, () -> IOUtils.copyLines((InputStream) null, GBK, new ArrayList<String>()));
	}

	@Test
	public void copyLines_toListFromReaderBytesAndChars() throws IOException {
		List<String> lines = new ArrayList<String>();
		IOUtils.copyLines(new StringReader("a\n\nb"), lines);
		assertEquals(Arrays.asList("a", "", "b"), lines);

		lines.clear();
		IOUtils.copyLines(CHINESE.getBytes(StandardCharsets.UTF_8), "UTF-8", lines);
		assertEquals(Collections.singletonList(CHINESE), lines);

		lines.clear();
		IOUtils.copyLines("1\n2".toCharArray(), lines);
		assertEquals(Arrays.asList("1", "2"), lines);

		assertThrows(IllegalArgumentException.class, () -> IOUtils.copyLines((byte[]) null, GBK, new ArrayList<String>()));
		assertThrows(IllegalArgumentException.class, () -> IOUtils.copyLines((java.io.Reader) null, new ArrayList<String>()));
		assertThrows(IllegalArgumentException.class, () -> IOUtils.copyLines(new StringReader(""), (List<String>) null));
	}

	// ---------------------------------------------------------------- writeLines

	@Test
	public void writeLines_mapUsesSeparator() throws IOException {
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("a", "1");
		map.put("b", "中");
		StringWriter writer = new StringWriter();
		IOUtils.writeLines(map, writer);
		assertEquals("a=1" + NL + "b=中" + NL, writer.toString());

		writer = new StringWriter();
		IOUtils.writeLines(map, writer, ":");
		assertEquals("a:1" + NL + "b:中" + NL, writer.toString());

		TrackingOutputStream out = new TrackingOutputStream();
		IOUtils.writeLines(map, out, "GBK");
		assertEquals("a=1" + NL + "b=中" + NL, new String(out.toByteArray(), GBK));
		assertFalse(out.isClosed());

		out = new TrackingOutputStream();
		IOUtils.writeLines(map, out, StandardCharsets.UTF_8, "->");
		assertEquals("a->1" + NL + "b->中" + NL, new String(out.toByteArray(), StandardCharsets.UTF_8));
	}

	@Test
	public void writeLines_enumerationIteratorAndCollection() throws IOException {
		Vector<String> vector = new Vector<String>(Arrays.asList("x", "中"));
		StringWriter writer = new StringWriter();
		IOUtils.writeLines(vector.elements(), writer);
		assertEquals("x" + NL + "中" + NL, writer.toString());

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		IOUtils.writeLines(vector.elements(), out, "GBK");
		assertEquals("x" + NL + "中" + NL, new String(out.toByteArray(), GBK));

		Iterator<String> it = vector.iterator();
		writer = new StringWriter();
		IOUtils.writeLines(it, writer);
		assertEquals("x" + NL + "中" + NL, writer.toString());

		out = new ByteArrayOutputStream();
		IOUtils.writeLines(vector.iterator(), out, "UTF-8");
		assertEquals("x" + NL + "中" + NL, new String(out.toByteArray(), StandardCharsets.UTF_8));

		writer = new StringWriter();
		IOUtils.writeLines((java.util.Collection<String>) vector, writer);
		assertEquals("x" + NL + "中" + NL, writer.toString());

		out = new ByteArrayOutputStream();
		IOUtils.writeLines((java.util.Collection<String>) vector, out, "UTF-8");
		assertEquals("x" + NL + "中" + NL, new String(out.toByteArray(), StandardCharsets.UTF_8));

		writer = new StringWriter();
		IOUtils.writeLines(Collections.<String>emptyList(), writer);
		assertEquals("", writer.toString());
	}

	// ---------------------------------------------------------------- range

	@Test
	public void checkRange_acceptsPositiveStartWithNegativeOrPositiveLimit() throws IOException {
		IOUtils.checkRange(1, -1);
		IOUtils.checkRange(1, 5);
		assertThrows(IOException.class, () -> IOUtils.checkRange(0, 5));
		assertThrows(IOException.class, () -> IOUtils.checkRange(1, 0));
		IOException e = assertThrows(IOException.class, () -> IOUtils.checkRange(Integer.MAX_VALUE, 1));
		assertEquals("Invalid range input: " + Integer.MAX_VALUE + ", 1", e.getMessage());
	}

	@Test
	public void isValidLine_limitsByStartAndLimit() {
		assertTrue(IOUtils.isValidLine(1, -1, 100));
		assertTrue(IOUtils.isValidLine(2, 3, 3));
		assertFalse(IOUtils.isValidLine(2, 3, 4));
	}

	@Test
	public void rangeCopyLines_toListReadsRequestedWindow() throws IOException {
		List<String> lines = new ArrayList<String>();
		IOUtils.rangeCopyLines(new LineNumberReader(new StringReader("1\n2\n3\n4\n5")), 2, 3, lines);
		assertEquals(Arrays.asList("2", "3", "4"), lines);

		lines.clear();
		IOUtils.rangeCopyLines(new LineNumberReader(new StringReader("1\n2\n3")), 2, -1, lines);
		assertEquals(Arrays.asList("2", "3"), lines);

		lines.clear();
		IOUtils.rangeCopyLines(new LineNumberReader(new StringReader("1\n2")), 5, 2, lines);
		assertTrue(lines.isEmpty());

		assertThrows(IOException.class, () -> IOUtils.rangeCopyLines(new LineNumberReader(new StringReader("")), 0, 1, new ArrayList<String>()));
		assertThrows(IllegalArgumentException.class, () -> IOUtils.rangeCopyLines(null, 1, 1, new ArrayList<String>()));
		assertThrows(IllegalArgumentException.class,
				() -> IOUtils.rangeCopyLines(new LineNumberReader(new StringReader("")), 1, 1, (List<String>) null));
	}

	@Test
	public void rangeCopyLines_toStringBufferAndBuilder() throws IOException {
		StringBuffer buffer = new StringBuffer();
		IOUtils.rangeCopyLines(new LineNumberReader(new StringReader("1\n2\n3\n4")), 2, 2, buffer);
		assertEquals("2" + NL + "3", buffer.toString());

		StringBuilder builder = new StringBuilder();
		IOUtils.rangeCopyLines(new LineNumberReader(new StringReader("1\n2\n3\n4")), 1, 2, builder);
		assertEquals("1" + NL + "2", builder.toString());

		// reaching the end of the stream before the limit keeps the trailing line separator
		builder = new StringBuilder();
		IOUtils.rangeCopyLines(new LineNumberReader(new StringReader("1\n2")), 2, 5, builder);
		assertEquals("2" + NL, builder.toString());

		buffer = new StringBuffer();
		IOUtils.rangeCopyLines(new LineNumberReader(new StringReader("1\n2\n3")), 3, -1, buffer);
		assertEquals("3" + NL, buffer.toString());

		assertThrows(IllegalArgumentException.class, () -> IOUtils.rangeCopyLines(null, 1, 1, new StringBuffer()));
		assertThrows(IllegalArgumentException.class, () -> IOUtils.rangeCopyLines(null, 1, 1, new StringBuilder()));
		assertThrows(IllegalArgumentException.class,
				() -> IOUtils.rangeCopyLines(new LineNumberReader(new StringReader("")), 1, 1, (StringBuffer) null));
		assertThrows(IllegalArgumentException.class,
				() -> IOUtils.rangeCopyLines(new LineNumberReader(new StringReader("")), 1, 1, (StringBuilder) null));
	}

	@Test
	public void rangeCopyLines_withHugeStartDoesNotAllocateMarkBuffer() throws IOException {
		List<String> lines = new ArrayList<String>();
		IOUtils.rangeCopyLines(new LineNumberReader(new StringReader("1\n2")), Integer.MAX_VALUE - 1, -1, lines);
		assertTrue(lines.isEmpty());
	}

	// ---------------------------------------------------------------- wrappers

	@Test
	public void getBufferedStreams_reuseExistingInstances() {
		BufferedInputStream bis = new BufferedInputStream(IOUtils.emptyInputStream());
		assertSame(bis, IOUtils.getBufferedInputStream(bis));
		assertNotSame(bis, IOUtils.getBufferedInputStream(IOUtils.emptyInputStream()));
		BufferedOutputStream bos = new BufferedOutputStream(new ByteArrayOutputStream());
		assertSame(bos, IOUtils.getBufferedOutputStream(bos));
		assertTrue(IOUtils.getBufferedOutputStream(new ByteArrayOutputStream()) instanceof BufferedOutputStream);
		assertThrows(IllegalArgumentException.class, () -> IOUtils.getBufferedInputStream(null));
		assertThrows(IllegalArgumentException.class, () -> IOUtils.getBufferedOutputStream(null));
	}

	@Test
	public void getReadersAndWriters_reuseExistingInstances() throws IOException {
		BufferedReader br = new BufferedReader(new StringReader(""));
		assertSame(br, IOUtils.getBufferedReader(br));
		LineNumberReader lnr = new LineNumberReader(new StringReader(""));
		assertSame(lnr, IOUtils.getLineNumberReader(lnr));
		assertTrue(IOUtils.getLineNumberReader(new StringReader("")) instanceof LineNumberReader);
		BufferedWriter bw = new BufferedWriter(new StringWriter());
		assertSame(bw, IOUtils.getBufferedWriter(bw));
		assertThrows(IllegalArgumentException.class, () -> IOUtils.getBufferedReader((java.io.Reader) null));
		assertThrows(IllegalArgumentException.class, () -> IOUtils.getLineNumberReader((java.io.Reader) null));
		assertThrows(IllegalArgumentException.class, () -> IOUtils.getBufferedWriter((java.io.Writer) null));

		byte[] gbk = CHINESE.getBytes(GBK);
		assertEquals(CHINESE, IOUtils.getBufferedReader(new ByteArrayInputStream(gbk), "GBK").readLine());
		assertEquals(CHINESE, IOUtils.getBufferedReader(new ByteArrayInputStream(gbk), "GBK", 16).readLine());
		assertEquals(CHINESE, IOUtils.getLineNumberReader(new ByteArrayInputStream(gbk), "GBK").readLine());
		assertEquals(CHINESE, IOUtils.getLineNumberReader(new ByteArrayInputStream(gbk), "GBK", 16).readLine());
		assertEquals(CHINESE, IOUtils.getLineNumberReader(new ByteArrayInputStream(gbk), GBK).readLine());
		assertThrows(IllegalArgumentException.class, () -> IOUtils.getBufferedReader((InputStream) null, GBK));
		assertThrows(IllegalArgumentException.class, () -> IOUtils.getLineNumberReader((InputStream) null, GBK));

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		BufferedWriter writer = IOUtils.getBufferedWriter(out, "GBK");
		writer.write(CHINESE);
		writer.flush();
		assertArrayEquals(gbk, out.toByteArray());
		out = new ByteArrayOutputStream();
		writer = IOUtils.getBufferedWriter(out, "UTF-8", 4);
		writer.write(CHINESE);
		writer.flush();
		assertEquals(CHINESE, new String(out.toByteArray(), StandardCharsets.UTF_8));
	}

	@Test
	public void objectAndDataStreams_reuseExistingInstancesAndRoundTrip() throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = IOUtils.getObjectOutputStream(bos);
		assertSame(oos, IOUtils.getObjectOutputStream(oos));
		oos.writeObject("value");
		oos.close();
		ObjectInputStream ois = IOUtils.getObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
		assertSame(ois, IOUtils.getObjectInputStream(ois));
		assertEquals("value", ois.readObject());

		bos = new ByteArrayOutputStream();
		DataOutputStream dos = IOUtils.getDataOutputStream(bos);
		assertSame(dos, IOUtils.getDataOutputStream(dos));
		dos.writeInt(42);
		DataInputStream dis = IOUtils.getDataInputStream(new ByteArrayInputStream(bos.toByteArray()));
		assertSame(dis, IOUtils.getDataInputStream(dis));
		assertEquals(42, dis.readInt());
	}

	@Test
	public void gzipStreams_roundTripAndReuseInstances() throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		GZIPOutputStream gos = IOUtils.getGZIPOutputStream(bos);
		assertSame(gos, IOUtils.getGZIPOutputStream(gos));
		gos.write(CHINESE.getBytes(StandardCharsets.UTF_8));
		gos.close();
		GZIPInputStream gis = IOUtils.getGZIPInputStream(new ByteArrayInputStream(bos.toByteArray()));
		assertSame(gis, IOUtils.getGZIPInputStream(gis));
		assertEquals(CHINESE, IOUtils.toString(gis, StandardCharsets.UTF_8));
	}

	@Test
	public void emptyBufferAndStream() throws IOException {
		assertEquals(0, IOUtils.emptyByteBuffer().capacity());
		assertEquals(-1, IOUtils.emptyInputStream().read());
	}

	@Test
	public void charArrayWriterRoundTrip() throws IOException {
		CharArrayWriter writer = new CharArrayWriter();
		IOUtils.copy(new StringReader("xyz"), writer);
		assertEquals("xyz", writer.toString());
	}
}
