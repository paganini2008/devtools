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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.github.paganini2008.devtools.io.IoTestSupport.FailingOutputStream;
import com.github.paganini2008.devtools.io.IoTestSupport.TrackingInputStream;
import com.github.paganini2008.devtools.io.IoTestSupport.TrackingOutputStream;

/**
 * SerializationUtilsTest
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class SerializationUtilsTest {

	@TempDir
	File tempDir;

	static class Person implements Serializable {

		private static final long serialVersionUID = 1L;
		final String name;
		final List<String> tags;

		Person(String name, String... tags) {
			this.name = name;
			this.tags = new ArrayList<String>(Arrays.asList(tags));
		}
	}

	@Test
	public void copy_returnsDeepCopy() {
		Person original = new Person("张三", "a", "b");
		Person copy = SerializationUtils.copy(original);
		assertNotSame(original, copy);
		assertNotSame(original.tags, copy.tags);
		assertEquals("张三", copy.name);
		assertEquals(original.tags, copy.tags);
	}

	@Test
	public void copyMany_returnsIndependentCopies() {
		Person original = new Person("p", "x");
		List<Person> copies = SerializationUtils.copyMany(original, 3);
		assertEquals(3, copies.size());
		assertNotSame(copies.get(0), copies.get(1));
		copies.get(0).tags.add("changed");
		assertEquals(1, copies.get(1).tags.size());
		assertTrue(SerializationUtils.copyMany(original, 0).isEmpty());
		assertTrue(SerializationUtils.copyMany(original, -1).isEmpty());
	}

	@Test
	public void serialize_roundTripsWithAndWithoutCompression() {
		for (boolean compress : new boolean[] { false, true }) {
			byte[] bytes = SerializationUtils.serialize("中文 value", compress);
			assertEquals("中文 value", SerializationUtils.deserialize(bytes, compress));
		}
		byte[] gzip = SerializationUtils.serialize("x", true);
		assertEquals(0x1f, gzip[0] & 0xff);
		assertEquals(0x8b, gzip[1] & 0xff);
	}

	@Test
	public void writeObject_rejectsNonSerializableObjects() {
		SerializationException e = assertThrows(SerializationException.class, () -> SerializationUtils.serialize(new Object(), false));
		assertEquals("Not serialiazble object", e.getMessage());
		assertThrows(SerializationException.class, () -> SerializationUtils.serialize(null, false));
		assertThrows(IllegalArgumentException.class, () -> SerializationUtils.writeObject("x", (java.io.OutputStream) null, false));
	}

	@Test
	public void writeObject_closesStreamAndPropagatesFailures() {
		TrackingOutputStream out = new TrackingOutputStream();
		SerializationUtils.writeObject("v", out, true);
		assertTrue(out.isClosed());
		assertEquals("v", SerializationUtils.deserialize(out.toByteArray(), true));
		SerializationException e = assertThrows(SerializationException.class,
				() -> SerializationUtils.writeObject("v", new FailingOutputStream(), false));
		assertTrue(e.getCause() instanceof IOException);
	}

	@Test
	public void readObject_closesStreamAndWrapsInvalidData() {
		TrackingInputStream in = new TrackingInputStream(SerializationUtils.serialize(42, false));
		assertEquals(42, SerializationUtils.readObject(in));
		assertTrue(in.isClosed());
		assertThrows(SerializationException.class, () -> SerializationUtils.readObject(new ByteArrayInputStream(new byte[] { 1, 2, 3 })));
		assertThrows(SerializationException.class, () -> SerializationUtils.deserialize(new byte[] { 1, 2 }, true));
		assertThrows(IllegalArgumentException.class, () -> SerializationUtils.readObject((java.io.InputStream) null));
		assertThrows(IllegalArgumentException.class, () -> SerializationUtils.deserialize(null, false));
	}

	@Test
	public void writeAndReadObject_viaFiles() {
		File file = new File(tempDir, "obj/person.ser");
		SerializationUtils.writeObject(new Person("李四", "t"), file, true);
		Person person = (Person) SerializationUtils.readObject(file, true);
		assertEquals("李四", person.name);
		SerializationException e = assertThrows(SerializationException.class,
				() -> SerializationUtils.readObject(new File(tempDir, "none"), false));
		assertTrue(e.getCause() instanceof FileNotFoundException);
		assertThrows(SerializationException.class, () -> SerializationUtils.writeObject("x", tempDir, false));
		assertThrows(IllegalArgumentException.class, () -> SerializationUtils.writeObject("x", (File) null, false));
		assertThrows(IllegalArgumentException.class, () -> SerializationUtils.readObject((File) null, false));
	}

	@Test
	public void fileSerialization_doesNotLeakFileHandles() throws Exception {
		final File file = new File(tempDir, "leak.ser");
		IoTestSupport.assertNoFileHandleLeak(() -> {
			SerializationUtils.writeObject("x", file, false);
			SerializationUtils.readObject(file, false);
		});
	}

	@Test
	public void exceptions_keepMessageAndCause() {
		Throwable cause = new IOException("io");
		assertEquals("m", new SerializationException("m").getMessage());
		assertSame(cause, new SerializationException("m", cause).getCause());
		assertSame(cause, new SerializationException(cause).getCause());
		assertEquals("c", new ConfigError("c").getMessage());
		assertSame(cause, new ConfigError("c", cause).getCause());
		assertSame(cause, new ConfigError(cause).getCause());
	}

	@Test
	public void readObject_closesStreamEvenWhenHeaderIsCorrupt() {
		// the ObjectInputStream constructor fails while reading the stream header
		TrackingInputStream in = new TrackingInputStream(new byte[] { 1, 2, 3 });
		assertThrows(SerializationException.class, () -> SerializationUtils.readObject(in));
		assertTrue(in.isClosed());
		TrackingInputStream gz = new TrackingInputStream(new byte[] { 1, 2, 3 });
		assertThrows(SerializationException.class, () -> SerializationUtils.readObject(gz, true));
		assertTrue(gz.isClosed());
	}

	@Test
	public void writeObject_closesStreamWhenHeaderCannotBeWritten() {
		final int[] closed = new int[1];
		FailingOutputStream out = new FailingOutputStream() {
			@Override
			public void close() throws IOException {
				closed[0]++;
				super.close();
			}
		};
		assertThrows(SerializationException.class, () -> SerializationUtils.writeObject("v", out, false));
		assertEquals(1, closed[0]);
	}

	@Test
	public void writeObjectToFile_nonSerializableDoesNotCreateOrTruncateFile() throws IOException {
		File missing = new File(tempDir, "sub/missing.ser");
		assertThrows(SerializationException.class, () -> SerializationUtils.writeObject(new Object(), missing, false));
		assertTrue(!missing.exists() && !missing.getParentFile().exists());

		File existing = new File(tempDir, "existing.ser");
		SerializationUtils.writeObject("keep", existing, false);
		assertThrows(SerializationException.class, () -> SerializationUtils.writeObject(new Object(), existing, false));
		assertEquals("keep", SerializationUtils.readObject(existing, false));
	}
}
