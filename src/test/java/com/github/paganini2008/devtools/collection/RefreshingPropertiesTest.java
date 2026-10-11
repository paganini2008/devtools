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
package com.github.paganini2008.devtools.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.github.paganini2008.devtools.MatchMode;

/**
 * RefreshingPropertiesTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class RefreshingPropertiesTest {

	static class CountingProperties extends EventBasedRefreshingProperties {

		private static final long serialVersionUID = 1L;
		final AtomicInteger version = new AtomicInteger();
		volatile boolean fail;

		@Override
		protected Properties createObject() throws Exception {
			if (fail) {
				throw new IllegalStateException("failed");
			}
			Properties p = new Properties();
			p.setProperty("app.name", "demo");
			p.setProperty("app.version", String.valueOf(version.get()));
			return p;
		}
	}

	@Test
	public void testBeforeRefreshIsNullSafe() {
		CountingProperties p = new CountingProperties();
		assertTrue(p.isEmpty());
		assertEquals(0, p.size());
		assertNull(p.getProperty("app.name"));
		assertEquals("x", p.getProperty("app.name", "x"));
		assertFalse(p.containsKey("app.name"));
		assertEquals("{}", p.toString());
	}

	@Test
	public void testRefreshAndEvents() throws Exception {
		CountingProperties p = new CountingProperties();
		final List<PropertyChangeEvent<Properties>> events = new ArrayList<PropertyChangeEvent<Properties>>();
		p.addEventListener(events::add);
		assertFalse(p.refresh());
		assertTrue(events.isEmpty());
		assertEquals("demo", p.getProperty("app.name"));
		assertFalse(p.refresh());
		p.version.incrementAndGet();
		assertTrue(p.refresh());
		assertEquals(1, events.size());
		assertEquals("1", events.get(0).getLatestVersion().getProperty("app.version"));
		assertEquals("0", events.get(0).getCurrentVersion().getProperty("app.version"));
		assertTrue(p.execute());
		p.fail = true;
		assertFalse(p.execute());
		assertEquals("1", p.getProperty("app.version"));
	}

	@Test
	public void testReadOperations(@TempDir Path dir) throws Exception {
		CountingProperties p = new CountingProperties();
		p.refresh();
		assertTrue(p.containsKey("app.name"));
		assertTrue(p.containsValue("demo"));
		assertTrue(p.contains("demo"));
		assertEquals("demo", p.get("app.name"));
		assertEquals("d", p.getOrDefault("missing", "d"));
		assertFalse(p.isEmpty());
		assertEquals(2, p.size());
		assertEquals(2, p.values().size());
		assertEquals(2, p.keySet().size());
		assertEquals(2, p.entrySet().size());
		assertEquals(2, Collections.list(p.keys()).size());
		assertEquals(2, Collections.list(p.elements()).size());
		assertEquals(2, Collections.list(p.propertyNames()).size());
		assertEquals(2, p.stringPropertyNames().size());
		assertEquals("demo", p.toMap().get("app.name"));
		assertEquals(1, p.toMap("name", MatchMode.END).size());
		assertTrue(p.toString().contains("app.name=demo"));

		File file = dir.resolve("out.properties").toFile();
		p.store(file, "test");
		Properties loaded = new Properties();
		loaded.load(new StringReader(new String(Files.readAllBytes(file.toPath()), StandardCharsets.ISO_8859_1)));
		assertEquals("demo", loaded.getProperty("app.name"));
		StringWriter writer = new StringWriter();
		p.store(writer, null);
		assertTrue(writer.toString().contains("app.name=demo"));
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		p.store(out, null);
		assertTrue(out.size() > 0);
		out.reset();
		p.storeToXML(out, null);
		assertTrue(out.toString("UTF-8").contains("app.name"));
		out.reset();
		p.storeToXML(out, null, "UTF-8");
		assertTrue(out.toString("UTF-8").contains("demo"));
		out.reset();
		p.list(new PrintStream(out, true, "UTF-8"));
		assertTrue(out.toString("UTF-8").contains("app.name=demo"));
		StringWriter listed = new StringWriter();
		p.list(new PrintWriter(listed, true));
		assertTrue(listed.toString().contains("app.name=demo"));
	}

	@Test
	public void testReadonly() {
		CountingProperties p = new CountingProperties();
		assertThrows(UnsupportedOperationException.class, () -> p.put("a", "b"));
		assertThrows(UnsupportedOperationException.class, () -> p.putIfAbsent("a", "b"));
		assertThrows(UnsupportedOperationException.class, () -> p.setProperty("a", "b"));
		assertThrows(UnsupportedOperationException.class, () -> p.load(new StringReader("a=b")));
		assertThrows(UnsupportedOperationException.class, () -> p.load(new ByteArrayInputStream(new byte[0])));
		assertThrows(UnsupportedOperationException.class, () -> p.loadFromXML(new ByteArrayInputStream(new byte[0])));
		assertThrows(UnsupportedOperationException.class, () -> p.save(new ByteArrayOutputStream(), null));
	}

	@Test
	public void testInterval() throws Exception {
		final CountDownLatch latch = new CountDownLatch(2);
		CountingProperties p = new CountingProperties() {

			private static final long serialVersionUID = 1L;

			@Override
			protected Properties createObject() throws Exception {
				latch.countDown();
				return super.createObject();
			}
		};
		p.setInterval(20, TimeUnit.MILLISECONDS);
		p.setInterval(20, TimeUnit.MILLISECONDS);
		assertTrue(latch.await(5, TimeUnit.SECONDS));
		p.clearInterval();
		p.clearInterval();
		assertEquals("demo", p.getProperty("app.name"));
	}

	@Test
	public void testPropertyChangeEvent() {
		Properties latest = new Properties();
		Properties current = new Properties();
		PropertyChangeEvent<Properties> event = new PropertyChangeEvent<Properties>(latest, current);
		assertSame(latest, event.getLatestVersion());
		assertSame(current, event.getCurrentVersion());
		assertSame(latest, event.getSource());
	}

}
