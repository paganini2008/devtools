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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * LineIOTest: tests for LineIterator, LineReader, LineWriter and StringArrayWriter.
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class LineIOTest {

	private static final String NL = System.getProperty("line.separator");
	private static final Charset GBK = Charset.forName("GBK");

	@TempDir
	File tempDir;

	private File write(String name, String content, Charset charset) throws IOException {
		File file = new File(tempDir, name);
		Files.write(file.toPath(), content.getBytes(charset));
		return file;
	}

	// ---------------------------------------------------------------- LineIterator

	@Test
	public void lineIterator_iteratesAllLinesOfFile() throws IOException {
		File file = write("it.txt", "第一\n第二\r\n第三", GBK);
		LineIterator it = new LineIterator(file, "GBK");
		List<String> lines = new ArrayList<String>();
		while (it.hasNext()) {
			lines.add(it.next());
		}
		assertEquals(Arrays.asList("第一", "第二", "第三"), lines);
		assertFalse(it.hasNext());
		assertThrows(NoSuchElementException.class, it::nextLine);
		it.close();
	}

	@Test
	public void lineIterator_hasNextIsIdempotentAndCloseStopsIteration() {
		LineIterator it = new LineIterator(new ByteArrayInputStream("a\nb".getBytes(StandardCharsets.UTF_8)), "UTF-8");
		assertTrue(it.hasNext());
		assertTrue(it.hasNext());
		assertEquals("a", it.nextLine());
		it.close();
		assertFalse(it.hasNext());
		assertThrows(UnsupportedOperationException.class, it::remove);
	}

	@Test
	public void lineIterator_skipsInvalidLines() {
		LineIterator it = new LineIterator(new StringReader("# comment\nvalue\n\n# other\nlast")) {
			@Override
			protected boolean isValid(String line) {
				return !line.startsWith("#") && !line.isEmpty();
			}
		};
		List<String> lines = new ArrayList<String>();
		it.forEachRemaining(lines::add);
		assertEquals(Arrays.asList("value", "last"), lines);
	}

	@Test
	public void lineIterator_emptyInputAndMissingFile() {
		assertFalse(new LineIterator(new StringReader("")).hasNext());
		assertThrows(FileNotFoundException.class, () -> new LineIterator(new File(tempDir, "none"), "UTF-8"));
	}

	@Test
	public void lineIterator_wrapsReadFailureAndClosesReader() {
		final int[] closed = new int[1];
		Reader failing = new Reader() {
			@Override
			public int read(char[] cbuf, int off, int len) throws IOException {
				throw new IOException("broken");
			}

			@Override
			public void close() {
				closed[0]++;
			}
		};
		LineIterator it = new LineIterator(failing);
		IllegalStateException e = assertThrows(IllegalStateException.class, it::hasNext);
		assertEquals("broken", e.getCause().getMessage());
		assertEquals(1, closed[0]);
		assertFalse(it.hasNext());
	}

	// ---------------------------------------------------------------- LineReader

	@Test
	public void lineReader_readsInBatches() throws IOException {
		File file = write("batch.txt", "1\n2\n3\n4\n5\n", StandardCharsets.UTF_8);
		LineReader reader = new LineReader(file, 2);
		try {
			assertTrue(reader.hasNext());
			assertTrue(reader.hasNext());
			assertEquals(Arrays.asList("1", "2"), reader.next());
			assertEquals(Arrays.asList("3", "4"), reader.next());
			assertEquals(Collections.singletonList("5"), reader.next());
			assertFalse(reader.hasNext());
			assertEquals(Collections.emptyList(), reader.next());
			assertThrows(UnsupportedOperationException.class, reader::remove);
		} finally {
			reader.close();
		}
	}

	@Test
	public void lineReader_decodesConfiguredCharset() throws IOException {
		File gbk = write("gbk.txt", "中文\n测试", GBK);
		LineReader reader = new LineReader(gbk, 10);
		reader.setCharset(GBK);
		assertEquals(Arrays.asList("中文", "测试"), reader.next());
		reader.close();

		File utf8 = write("utf8.txt", "汉字", StandardCharsets.UTF_8);
		reader = new LineReader(utf8, 10);
		reader.setCharset(null);
		assertEquals(Collections.singletonList("汉字"), reader.next());
		reader.close();
		reader.close();
	}

	@Test
	public void lineReader_rejectsInvalidArgumentsAndFailsAfterClose() throws IOException {
		File file = write("x.txt", "x", StandardCharsets.UTF_8);
		assertThrows(IllegalArgumentException.class, () -> new LineReader(file, 0));
		assertThrows(FileNotFoundException.class, () -> new LineReader(new File(tempDir, "none"), 1));
		LineReader reader = new LineReader(file, 1);
		reader.close();
		assertThrows(IllegalStateException.class, reader::hasNext);
		assertThrows(IllegalStateException.class, reader::next);
	}

	@Test
	public void lineReader_emptyFileHasNoLines() throws IOException {
		LineReader reader = new LineReader(write("empty.txt", "", StandardCharsets.UTF_8), 3);
		assertFalse(reader.hasNext());
		reader.close();
	}

	// ---------------------------------------------------------------- LineWriter

	@Test
	public void lineWriter_closeWritesQueuedLines() throws IOException {
		StringWriter out = new StringWriter();
		LineWriter writer = new LineWriter(out);
		writer.write("a");
		writer.write("中");
		writer.close();
		assertEquals("a" + NL + "中" + NL, out.toString());
	}

	@Test
	public void lineWriter_writesInBatchesPreservingOrder() throws IOException {
		StringWriter out = new StringWriter();
		LineWriter writer = new LineWriter(out, 2);
		for (int i = 0; i < 7; i++) {
			writer.write("line" + i);
		}
		writer.flush();
		StringBuilder expected = new StringBuilder();
		for (int i = 0; i < 7; i++) {
			expected.append("line").append(i).append(NL);
		}
		assertEquals(expected.toString(), out.toString());
		writer.write("x");
		writer.write("x");
		writer.flush();
		assertTrue(out.toString().endsWith("x" + NL + "x" + NL));
		writer.close();
	}

	@Test
	public void lineWriter_flushWithoutQueuedLinesIsNoop() throws IOException {
		StringWriter out = new StringWriter();
		LineWriter writer = new LineWriter(out, 1);
		writer.flush();
		assertEquals("", out.toString());
		writer.close();
		assertThrows(IOException.class, () -> writer.write(new char[] { 'a' }, 0, 1));
	}

	// ---------------------------------------------------------------- StringArrayWriter

	@Test
	public void stringArrayWriter_collectsEachWrite() throws IOException {
		StringArrayWriter writer = new StringArrayWriter();
		writer.write("first");
		writer.write("abcdef".toCharArray(), 1, 3);
		writer.write("xyz", 1, 2);
		writer.flush();
		assertArrayEquals(new String[] { "first", "bcd", "yz" }, writer.toArray());
		writer.close();
		assertEquals(0, writer.toArray().length);
	}

	@Test
	public void stringArrayWriter_capturesPrintWriterOutput() {
		StringArrayWriter out = new StringArrayWriter();
		PrintWriter writer = new PrintWriter(out);
		writer.print("hello");
		writer.flush();
		assertArrayEquals(new String[] { "hello" }, out.toArray());
	}
}
