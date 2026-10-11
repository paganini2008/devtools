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

import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;

/**
 * IoTestSupport: shared helpers for io tests, including streams that record close calls and an open file descriptor
 * counter used to detect leaked file handles.
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
final class IoTestSupport {

	private IoTestSupport() {
	}

	static class TrackingInputStream extends FilterInputStream {

		int closeCount;

		TrackingInputStream(byte[] data) {
			super(new ByteArrayInputStream(data));
		}

		@Override
		public void close() throws IOException {
			closeCount++;
			super.close();
		}

		boolean isClosed() {
			return closeCount > 0;
		}
	}

	static class TrackingOutputStream extends FilterOutputStream {

		int closeCount;
		int flushCount;
		final ByteArrayOutputStream buffer;

		TrackingOutputStream() {
			this(new ByteArrayOutputStream());
		}

		private TrackingOutputStream(ByteArrayOutputStream buffer) {
			super(buffer);
			this.buffer = buffer;
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			out.write(b, off, len);
		}

		@Override
		public void flush() throws IOException {
			flushCount++;
			super.flush();
		}

		@Override
		public void close() throws IOException {
			closeCount++;
			super.close();
		}

		boolean isClosed() {
			return closeCount > 0;
		}

		byte[] toByteArray() {
			return buffer.toByteArray();
		}
	}

	static class TrackingReader extends Reader {

		final Reader delegate;
		int closeCount;

		TrackingReader(String content) {
			this.delegate = new StringReader(content);
		}

		@Override
		public int read(char[] cbuf, int off, int len) throws IOException {
			return delegate.read(cbuf, off, len);
		}

		@Override
		public void close() throws IOException {
			closeCount++;
			delegate.close();
		}
	}

	static class TrackingWriter extends StringWriter {

		int closeCount;
		int flushCount;

		@Override
		public void flush() {
			flushCount++;
			super.flush();
		}

		@Override
		public void close() throws IOException {
			closeCount++;
			super.close();
		}
	}

	static class FailingOutputStream extends OutputStream {

		@Override
		public void write(int b) throws IOException {
			throw new IOException("write failed");
		}

		@Override
		public void flush() throws IOException {
			throw new IOException("flush failed");
		}

		@Override
		public void close() throws IOException {
			throw new IOException("close failed");
		}
	}

	static class FailingInputStream extends InputStream {

		@Override
		public int read() throws IOException {
			throw new IOException("read failed");
		}

		@Override
		public void close() throws IOException {
			throw new IOException("close failed");
		}
	}

	/**
	 * Returns the number of open file descriptors of the current JVM, or skips the calling test if the platform does
	 * not expose it.
	 */
	static long openFileDescriptors() {
		OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
		try {
			Class<?> type = Class.forName("com.sun.management.UnixOperatingSystemMXBean");
			assumeTrue(type.isInstance(os), "Open file descriptor count is not available");
			Method method = type.getMethod("getOpenFileDescriptorCount");
			return ((Number) method.invoke(os)).longValue();
		} catch (Exception e) {
			assumeTrue(false, "Open file descriptor count is not available");
			return -1;
		}
	}

	interface IoAction {
		void run() throws Exception;
	}

	/**
	 * Runs the action many times and asserts that the number of open file descriptors does not grow, i.e. every file
	 * handle opened by the action is closed again.
	 */
	static void assertNoFileHandleLeak(IoAction action) throws Exception {
		action.run();
		long before = openFileDescriptors();
		for (int i = 0; i < 100; i++) {
			action.run();
		}
		long after = openFileDescriptors();
		if (after - before > 10) {
			throw new AssertionError("File handles leaked: before=" + before + ", after=" + after);
		}
	}
}
