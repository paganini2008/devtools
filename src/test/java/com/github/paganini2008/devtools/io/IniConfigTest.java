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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.github.paganini2008.devtools.MatchMode;

/**
 * IniConfigTest: tests for BasicIniConfig and the refreshing ini config family.
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class IniConfigTest {

	private static final Charset GBK = Charset.forName("GBK");

	private static final String INI = "# leading comment\n" //
			+ "orphan=ignored\n" //
			+ "[server]\n" //
			+ "host = localhost  # inline comment\n" //
			+ "port=8080\n" //
			+ "\n" //
			+ "timeout.ms=1.5\n" //
			+ "enabled=true\n" //
			+ "=novalue\n" //
			+ "noequals\n" //
			+ "[]\n" //
			+ "[名称]\n" //
			+ "标题=中文值\n" //
			+ "small=7\n";

	@TempDir
	File tempDir;

	private File write(String name, String content, Charset charset) throws IOException {
		File file = new File(tempDir, name);
		file.getParentFile().mkdirs();
		Files.write(file.toPath(), content.getBytes(charset));
		return file;
	}

	private BasicIniConfig load(Charset charset) throws IOException {
		BasicIniConfig config = new BasicIniConfig();
		config.load(write("c.ini", INI, charset), charset.name());
		return config;
	}

	@Test
	public void load_parsesSectionsKeysAndComments() throws IOException {
		BasicIniConfig config = load(StandardCharsets.UTF_8);
		assertEquals(new HashSet<String>(Arrays.asList("server", "名称")), new HashSet<String>(Arrays.asList(config.sections())));
		Map<String, String> server = config.get("server");
		assertEquals("localhost", server.get("host"));
		assertEquals(4, server.size());
		assertFalse(server.containsKey("orphan"));
		assertEquals("中文值", config.getString("名称", "标题"));
	}

	@Test
	public void load_decodesGbk() throws IOException {
		assertEquals("中文值", load(GBK).getString("名称", "标题"));
	}

	@Test
	public void load_missingFileFails() {
		assertThrows(FileNotFoundException.class, () -> new BasicIniConfig().load(new File(tempDir, "none.ini"), "UTF-8"));
	}

	@Test
	public void typedGetters_convertValuesAndUseDefaults() throws IOException {
		BasicIniConfig config = load(StandardCharsets.UTF_8);
		assertEquals("localhost", config.getString("server", "host"));
		assertEquals("def", config.getString("server", "missing", "def"));
		assertNull(config.getString("server", "missing"));
		assertEquals("def", config.getString("nosection", "host", "def"));
		assertEquals("def", config.getString(null, "host", "def"));
		assertEquals("def", config.getString("server", null, "def"));
		assertEquals(Integer.valueOf(8080), config.getInteger("server", "port"));
		assertEquals(Integer.valueOf(1), config.getInteger("server", "none", 1));
		assertEquals(Long.valueOf(8080L), config.getLong("server", "port"));
		assertEquals(Long.valueOf(2L), config.getLong("server", "none", 2L));
		assertEquals(Short.valueOf((short) 8080), config.getShort("server", "port"));
		assertEquals(Short.valueOf((short) 3), config.getShort("server", "none", (short) 3));
		assertEquals(Byte.valueOf((byte) 7), config.getByte("名称", "small"));
		assertEquals(Byte.valueOf((byte) 4), config.getByte("名称", "none", (byte) 4));
		assertEquals(Float.valueOf(1.5f), config.getFloat("server", "timeout.ms"));
		assertEquals(Float.valueOf(5f), config.getFloat("server", "none", 5f));
		assertEquals(Double.valueOf(1.5d), config.getDouble("server", "timeout.ms"));
		assertEquals(Double.valueOf(6d), config.getDouble("server", "none", 6d));
		assertEquals(Boolean.TRUE, config.getBoolean("server", "enabled"));
		assertEquals(Boolean.FALSE, config.getBoolean("server", "none", false));
		assertNull(config.getInteger("server", "none"));
		assertNull(config.getByte("x", "y"));
		assertNull(config.getShort("x", "y"));
		assertNull(config.getLong("x", "y"));
		assertNull(config.getFloat("x", "y"));
		assertNull(config.getDouble("x", "y"));
		assertNull(config.getBoolean("x", "y"));
	}

	@Test
	public void get_returnsCopiesAndEmptyMapForUnknownSection() throws IOException {
		BasicIniConfig config = load(StandardCharsets.UTF_8);
		config.get("server").put("host", "changed");
		assertEquals("localhost", config.getString("server", "host"));
		assertTrue(config.get("missing").isEmpty());
		assertTrue(config.get(null).isEmpty());
		Map<String, String> matched = config.get("server", "port", MatchMode.START);
		assertEquals(1, matched.size());
		assertEquals("8080", matched.get("port"));
	}

	@Test
	public void set_updatesValuesAndSections() throws IOException {
		BasicIniConfig config = new BasicIniConfig();
		assertNull(config.set("a", "k", "v"));
		assertEquals("v", config.set("a", "k", "w"));
		Map<String, String> section = new HashMap<String, String>();
		section.put("x", "1");
		assertNull(config.set("b", section));
		assertEquals("1", config.getString("b", "x"));
		List<String> names = new ArrayList<String>();
		for (Iterator<String> it = config.iterator(); it.hasNext();) {
			names.add(it.next());
		}
		assertEquals(new HashSet<String>(Arrays.asList("a", "b")), new HashSet<String>(names));
		assertEquals(2, config.toMap().size());
		assertTrue(config.toString().contains("k=w"));
	}

	@Test
	public void store_writesIniThatCanBeLoadedAgain() throws IOException {
		BasicIniConfig config = load(StandardCharsets.UTF_8);
		File file = new File(tempDir, "out/stored.ini");
		config.store(file, "GBK");
		BasicIniConfig reloaded = new BasicIniConfig();
		reloaded.load(file, "GBK");
		assertEquals(config.toMap(), reloaded.toMap());

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		config.store(out, "UTF-8");
		String text = new String(out.toByteArray(), StandardCharsets.UTF_8);
		assertTrue(text.contains("[server]"));
		assertTrue(text.contains("标题=中文值"));
	}

	@Test
	public void basicIniConfig_isSerializable() throws IOException {
		BasicIniConfig config = load(StandardCharsets.UTF_8);
		BasicIniConfig copy = SerializationUtils.copy(config);
		assertEquals(config.toMap(), copy.toMap());
	}

	@Test
	public void loadFromClassPath_firstClassPathEntryWins() throws IOException {
		write("cp1/app.ini", "[s]\nk=first\nonly1=1\n", StandardCharsets.UTF_8);
		write("cp2/app.ini", "[s]\nk=second\nonly2=2\n", StandardCharsets.UTF_8);
		try (URLClassLoader loader = new URLClassLoader(
				new URL[] { new File(tempDir, "cp1").toURI().toURL(), new File(tempDir, "cp2").toURI().toURL() }, null)) {
			BasicIniConfig config = new BasicIniConfig();
			config.loadFromClassPath("app.ini", "UTF-8", loader);
			assertEquals("first", config.getString("s", "k"));
			assertEquals("1", config.getString("s", "only1"));
			assertEquals("2", config.getString("s", "only2"));

			ClassPathIniConfig cp = new ClassPathIniConfig(new String[] { "app.ini" }, loader);
			cp.setCharset("UTF-8");
			assertEquals("first", cp.getString("s", "k"));
		}
		BasicIniConfig config = new BasicIniConfig();
		config.loadFromClassPath("no-such-resource.ini", "UTF-8", null);
		assertEquals(0, config.sections().length);
		assertEquals(0, new ClassPathIniConfig("no-such-resource.ini").sections().length);
		assertEquals(0, new ClassPathIniConfig().sections().length);
	}

	// ---------------------------------------------------------------- refreshing

	private static class RecordingIniConfig extends FileSystemIniConfig {

		private static final long serialVersionUID = 1L;
		final List<String> changes = new ArrayList<String>();

		RecordingIniConfig(File... files) {
			super(files);
		}

		@Override
		protected void onChange(String section, Map<String, String> latest, Map<String, String> current) {
			changes.add(section + ":" + (current == null ? "new" : "changed"));
		}
	}

	@Test
	public void refreshingConfig_loadsLazilyAndDelegatesAllGetters() throws IOException {
		File file = write("r.ini", INI, GBK);
		FileSystemIniConfig config = new FileSystemIniConfig(file);
		config.setCharset("GBK");
		assertEquals("中文值", config.getString("名称", "标题"));
		assertEquals("x", config.getString("server", "none", "x"));
		assertNull(config.getString("server", "none"));
		assertEquals(Integer.valueOf(8080), config.getInteger("server", "port"));
		assertNull(config.getInteger("server", "none"));
		assertEquals(Long.valueOf(8080), config.getLong("server", "port"));
		assertNull(config.getLong("server", "none"));
		assertEquals(Short.valueOf((short) 8080), config.getShort("server", "port"));
		assertNull(config.getShort("server", "none"));
		assertEquals(Byte.valueOf((byte) 7), config.getByte("名称", "small"));
		assertNull(config.getByte("server", "none"));
		assertEquals(Float.valueOf(1.5f), config.getFloat("server", "timeout.ms"));
		assertNull(config.getFloat("server", "none"));
		assertEquals(Double.valueOf(1.5), config.getDouble("server", "timeout.ms"));
		assertNull(config.getDouble("server", "none"));
		assertEquals(Boolean.TRUE, config.getBoolean("server", "enabled"));
		assertNull(config.getBoolean("server", "none"));
		assertEquals(2, config.sections().length);
		assertEquals(4, config.get("server").size());
		assertEquals(1, config.get("server", "host", MatchMode.ANY_WHERE).size());
		assertEquals("localhost", config.set("server", "host", "y"));
		assertEquals("y", config.getString("server", "host"));
		config.set("extra", new HashMap<String, String>());
		assertTrue(config.toMap().containsKey("extra"));
		assertTrue(config.iterator().hasNext());
		assertTrue(config.toString().contains("server"));
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		config.store(out, "UTF-8");
		assertTrue(new String(out.toByteArray(), StandardCharsets.UTF_8).contains("host=y"));
	}

	@Test
	public void refreshingConfig_failingLazyLoadThrowsConfigError() {
		FileSystemIniConfig config = new FileSystemIniConfig(new File(tempDir, "none.ini"));
		ConfigError error = assertThrows(ConfigError.class, () -> config.getString("a", "b"));
		assertTrue(error.getCause() instanceof FileNotFoundException);
	}

	@Test
	public void refresh_reportsChangedAndNewSections() throws Exception {
		File file = write("w.ini", "[a]\nk=1\n[b]\nk=1\n", StandardCharsets.UTF_8);
		RecordingIniConfig config = new RecordingIniConfig(file);
		assertFalse(config.refresh());
		assertFalse(config.refresh());
		write("w.ini", "[a]\nk=2\n[b]\nk=1\n[c]\nk=1\n", StandardCharsets.UTF_8);
		assertTrue(config.refresh());
		assertEquals(Arrays.asList("a:changed", "c:new"), config.changes);
		assertEquals("2", config.getString("a", "k"));
	}

	@Test
	public void realtimeConfig_delegatesAndRefreshesOnExecute() throws Exception {
		File file = write("rt.ini", INI, StandardCharsets.UTF_8);
		RecordingIniConfig delegate = new RecordingIniConfig(file);
		delegate.refresh();
		RealtimeRefreshingIniConfig config = delegate.refresh(1);
		assertEquals("localhost", config.getString("server", "host"));
		assertEquals("d", config.getString("server", "none", "d"));
		assertEquals(Integer.valueOf(8080), config.getInteger("server", "port"));
		assertEquals(Integer.valueOf(1), config.getInteger("server", "none", 1));
		assertEquals(Long.valueOf(8080), config.getLong("server", "port"));
		assertEquals(Long.valueOf(1), config.getLong("server", "none", 1L));
		assertEquals(Short.valueOf((short) 8080), config.getShort("server", "port"));
		assertEquals(Short.valueOf((short) 1), config.getShort("server", "none", (short) 1));
		assertEquals(Byte.valueOf((byte) 7), config.getByte("名称", "small"));
		assertEquals(Byte.valueOf((byte) 1), config.getByte("server", "none", (byte) 1));
		assertEquals(Float.valueOf(1.5f), config.getFloat("server", "timeout.ms"));
		assertEquals(Float.valueOf(1f), config.getFloat("server", "none", 1f));
		assertEquals(Double.valueOf(1.5), config.getDouble("server", "timeout.ms"));
		assertEquals(Double.valueOf(1), config.getDouble("server", "none", 1d));
		assertEquals(Boolean.TRUE, config.getBoolean("server", "enabled"));
		assertEquals(Boolean.FALSE, config.getBoolean("server", "none", false));
		assertEquals(2, config.sections().length);
		assertEquals(4, config.get("server").size());
		assertEquals(1, config.get("server", "port", MatchMode.END).size());
		config.set("server", "host", "remote");
		assertEquals("remote", config.getString("server", "host"));
		config.set("more", new HashMap<String, String>());
		assertEquals(3, config.toMap().size());
		assertTrue(config.iterator().hasNext());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		config.store(out, "UTF-8");
		assertTrue(out.size() > 0);

		write("rt.ini", "[server]\nhost=new\n", StandardCharsets.UTF_8);
		assertTrue(config.execute());
		assertEquals("new", config.getString("server", "host"));
		assertTrue(new File(tempDir, "rt.ini").delete());
		assertFalse(config.execute());

		config.start();
		config.start();
		config.close();
		config.close();
	}

	@Test
	public void realtimeConfig_isSerializableWithoutTimer() throws Exception {
		File file = write("s.ini", "[a]\nk=v\n", StandardCharsets.UTF_8);
		FileSystemIniConfig delegate = new FileSystemIniConfig(file);
		delegate.refresh();
		RealtimeRefreshingIniConfig config = delegate.refresh(60);
		config.start();
		try {
			RealtimeRefreshingIniConfig copy = SerializationUtils.copy(config);
			assertEquals("v", copy.getString("a", "k"));
		} finally {
			config.close();
		}
		assertArrayEquals(new String[] { "a" }, delegate.sections());
	}
}
