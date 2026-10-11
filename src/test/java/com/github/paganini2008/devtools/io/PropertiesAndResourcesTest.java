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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.github.paganini2008.devtools.MatchMode;

/**
 * PropertiesAndResourcesTest: tests for BasicProperties, the refreshing properties and resources, PropertiesUtils,
 * Resources and ResourceUtils.
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class PropertiesAndResourcesTest {

	private static final Charset GBK = Charset.forName("GBK");
	private static final String CONTENT = "db.url=jdbc:h2:mem\n" //
			+ "db.user=sa\n" //
			+ "app.name=中文应用\n" //
			+ "app.port=8080\n" //
			+ "app.ratio=0.5\n" //
			+ "app.enabled=true\n" //
			+ "app.small=3\n";

	@TempDir
	File tempDir;

	private URLClassLoader loader;
	private String resourceName;

	@BeforeEach
	public void setUp() throws IOException {
		// a unique resource name keeps the static caches of PropertiesUtils and ResourceUtils independent
		resourceName = "cfg-" + System.nanoTime() + ".properties";
		write("cp1/" + resourceName, CONTENT, StandardCharsets.UTF_8);
		write("cp2/" + resourceName, "db.user=other\nonly.second=yes\n", StandardCharsets.UTF_8);
		write("cp1/msg.properties", "greeting=hello\n", StandardCharsets.ISO_8859_1);
		write("cp1/msg_zh_CN.properties", "greeting=\\u4f60\\u597d\n", StandardCharsets.ISO_8859_1);
		write("cp1/other.properties", "farewell=bye\n", StandardCharsets.ISO_8859_1);
		loader = new URLClassLoader(new URL[] { new File(tempDir, "cp1").toURI().toURL(), new File(tempDir, "cp2").toURI().toURL() }, null);
	}

	@AfterEach
	public void tearDown() throws IOException {
		loader.close();
	}

	private File write(String name, String content, Charset charset) throws IOException {
		File file = new File(tempDir, name);
		file.getParentFile().mkdirs();
		Files.write(file.toPath(), content.getBytes(charset));
		return file;
	}

	// ---------------------------------------------------------------- BasicProperties

	@Test
	public void basicProperties_loadsFileWithCharset() throws IOException {
		BasicProperties p = new BasicProperties();
		p.load(write("gbk.properties", CONTENT, GBK), "GBK");
		assertEquals("中文应用", p.getProperty("app.name"));
		assertThrows(FileNotFoundException.class, () -> new BasicProperties().load(new File(tempDir, "none"), "UTF-8"));
	}

	@Test
	public void basicProperties_loadsClassPathResourcesFirstEntryWins() throws IOException {
		BasicProperties p = new BasicProperties();
		p.load(resourceName, "UTF-8", loader);
		assertEquals("sa", p.getProperty("db.user"));
		assertEquals("yes", p.getProperty("only.second"));
		BasicProperties system = new BasicProperties();
		system.load(resourceName, "UTF-8");
		assertTrue(system.isEmpty());
	}

	@Test
	public void basicProperties_filtersAndConvertsToMap() throws IOException {
		BasicProperties p = new BasicProperties();
		p.load(resourceName, "UTF-8", loader);
		BasicProperties db = p.filter("db");
		assertEquals("jdbc:h2:mem", db.getProperty("url"));
		assertEquals(2, db.size());
		BasicProperties app = p.filter("app.", MatchMode.START);
		assertEquals(5, app.size());
		assertEquals(8, p.toMap().size());
		assertEquals("sa", p.toMap("db").get("user"));
		assertEquals(5, p.toMap("app", MatchMode.START).size());
	}

	// ---------------------------------------------------------------- refreshing properties

	@Test
	public void fileSystemProperties_refreshDetectsChanges() throws Exception {
		File file = write("fs.properties", "k=1\n", GBK);
		final List<String> changes = new ArrayList<String>();
		FileSystemProperties p = new FileSystemProperties(file) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onChange(Properties latest, Properties current) {
				changes.add(current.getProperty("k") + "->" + latest.getProperty("k"));
			}
		};
		p.setCharset("GBK");
		assertFalse(p.refresh());
		assertEquals("1", p.getProperty("k"));
		write("fs.properties", "k=中\n", GBK);
		assertTrue(p.refresh());
		assertEquals(Collections.singletonList("1->中"), changes);
		assertEquals(0, new FileSystemProperties().createObject().size());
	}

	@Test
	public void classPathProperties_loadsWithLoader() throws Exception {
		ClassPathProperties p = new ClassPathProperties(new String[] { resourceName }, loader);
		p.setCharset("UTF-8");
		p.refresh();
		assertEquals("中文应用", p.getProperty("app.name"));
		ClassPathProperties none = new ClassPathProperties();
		none.refresh();
		assertEquals(0, none.size());
		ClassPathProperties missing = new ClassPathProperties("no-such.properties");
		missing.refresh();
		assertEquals(0, missing.size());
	}

	@Test
	public void realtimeProperties_delegatesReadOperations() throws Exception {
		File file = write("rt.properties", "a=1\nb=2\n", StandardCharsets.UTF_8);
		FileSystemProperties delegate = new FileSystemProperties(file);
		delegate.refresh();
		RealtimeRefreshingProperties p = new RealtimeRefreshingProperties(delegate, 60);
		assertTrue(p.containsKey("a"));
		assertTrue(p.containsValue("2"));
		assertEquals("1", p.get("a"));
		assertEquals("1", p.getProperty("a"));
		assertEquals("z", p.getProperty("none", "z"));
		assertEquals(2, p.size());
		assertEquals(2, p.stringPropertyNames().size());
		assertTrue(p.propertyNames().hasMoreElements());
		assertTrue(p.toString().contains("a=1"));
		StringWriter writer = new StringWriter();
		p.store(writer, "c");
		assertTrue(writer.toString().contains("a=1"));
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		p.store(out, "c");
		assertTrue(out.size() > 0);
		out = new ByteArrayOutputStream();
		p.storeToXML(out, "c");
		assertTrue(new String(out.toByteArray(), StandardCharsets.UTF_8).contains("<entry key=\"a\">1</entry>"));
		out = new ByteArrayOutputStream();
		p.storeToXML(out, "c", "UTF-8");
		assertTrue(out.size() > 0);
		out = new ByteArrayOutputStream();
		p.list(new PrintStream(out, true, "UTF-8"));
		assertTrue(out.size() > 0);
		writer = new StringWriter();
		p.list(new PrintWriter(writer));
		assertTrue(writer.toString().contains("a=1"));

		write("rt.properties", "a=9\n", StandardCharsets.UTF_8);
		assertTrue(p.execute());
		assertEquals("9", p.getProperty("a"));
		assertTrue(file.delete());
		assertFalse(p.execute());
		p.start();
		p.start();
		p.close();
		p.close();
	}

	// ---------------------------------------------------------------- PropertiesUtils

	@Test
	public void propertiesUtils_usesGivenClassLoaderAndCaches() throws IOException {
		Properties p = PropertiesUtils.getProperties(resourceName, loader);
		assertEquals("sa", p.getProperty("db.user"));
		assertSame(p, PropertiesUtils.getProperties(resourceName, loader));
		assertEquals("jdbc:h2:mem", PropertiesUtils.getProperties(resourceName, loader, "db").getProperty("url"));
		assertEquals(5, PropertiesUtils.getProperties(resourceName, loader, "app", MatchMode.START).size());
		Properties loaded = PropertiesUtils.loadProperties(resourceName, loader);
		assertNotSame(p, loaded);
		assertEquals(p, loaded);
	}

	@Test
	public void propertiesUtils_defaultLoaderReturnsEmptyForMissingResource() throws IOException {
		PropertiesUtils.setCharset("UTF-8");
		assertTrue(PropertiesUtils.loadProperties(resourceName).isEmpty());
		assertTrue(PropertiesUtils.getProperties(resourceName).isEmpty());
		assertTrue(PropertiesUtils.getProperties(resourceName, "db").isEmpty());
		assertTrue(PropertiesUtils.getProperties(resourceName, "db", MatchMode.START).isEmpty());
	}

	// ---------------------------------------------------------------- RefreshingResource

	@Test
	public void fileSystemResource_typedGettersAndDefaults() throws Exception {
		FileSystemResource resource = new FileSystemResource(write("r.properties", CONTENT, GBK));
		resource.setCharset("GBK");
		assertNull(resource.toMap());
		assertEquals("", resource.toString());
		assertEquals("d", resource.getString("db.user", "d"));
		assertFalse(resource.refresh());
		assertEquals("中文应用", resource.getString("app.name"));
		assertEquals("d", resource.getString("none", "d"));
		assertNull(resource.getString("none"));
		assertNull(resource.getString(null));
		assertEquals(Integer.valueOf(8080), resource.getInteger("app.port"));
		assertEquals(Integer.valueOf(1), resource.getInteger("none", 1));
		assertNull(resource.getInteger("none"));
		assertEquals(Long.valueOf(8080), resource.getLong("app.port"));
		assertEquals(Long.valueOf(1), resource.getLong("none", 1L));
		assertNull(resource.getLong("none"));
		assertEquals(Short.valueOf((short) 8080), resource.getShort("app.port"));
		assertEquals(Short.valueOf((short) 1), resource.getShort("none", (short) 1));
		assertNull(resource.getShort("none"));
		assertEquals(Byte.valueOf((byte) 3), resource.getByte("app.small"));
		assertEquals(Byte.valueOf((byte) 1), resource.getByte("none", (byte) 1));
		assertNull(resource.getByte("none"));
		assertEquals(Float.valueOf(0.5f), resource.getFloat("app.ratio"));
		assertEquals(Float.valueOf(1f), resource.getFloat("none", 1f));
		assertNull(resource.getFloat("none"));
		assertEquals(Double.valueOf(0.5), resource.getDouble("app.ratio"));
		assertEquals(Double.valueOf(1), resource.getDouble("none", 1d));
		assertNull(resource.getDouble("none"));
		assertEquals(Boolean.TRUE, resource.getBoolean("app.enabled"));
		assertEquals(Boolean.FALSE, resource.getBoolean("none", false));
		assertNull(resource.getBoolean("none"));
		assertEquals(7, resource.toMap().size());
		assertEquals(2, resource.toMap("db", MatchMode.START).size());
		assertTrue(resource.toString().contains("db.user=sa"));
		assertEquals(0, new FileSystemResource().getConfig().size());
	}

	@Test
	public void refreshingResource_beforeRefreshIsNullSafe() {
		FileSystemResource resource = new FileSystemResource(new File(tempDir, "none"));
		assertNull(resource.getInteger("x"));
		assertNull(resource.toMap("x", MatchMode.START));
	}

	@Test
	public void refreshingResource_notifiesChangesAndStores() throws Exception {
		File file = write("c.properties", "k=1\n", StandardCharsets.UTF_8);
		final List<String> changes = new ArrayList<String>();
		FileSystemResource resource = new FileSystemResource(file) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onChange(Map<String, String> latest, Map<String, String> current) {
				changes.add(current.get("k") + "->" + latest.get("k"));
			}
		};
		resource.refresh();
		write("c.properties", "k=2\n", StandardCharsets.UTF_8);
		assertTrue(resource.refresh());
		assertFalse(resource.refresh());
		assertEquals(Collections.singletonList("1->2"), changes);

		File stored = new File(tempDir, "stored/out.properties");
		resource.store(stored, "UTF-8");
		Properties reloaded = new Properties();
		reloaded.load(Files.newBufferedReader(stored.toPath(), StandardCharsets.ISO_8859_1));
		assertEquals("2", reloaded.getProperty("k"));
	}

	@Test
	public void classPathResource_usesLoaderAndCharset() throws Exception {
		ClassPathResource resource = new ClassPathResource(new String[] { resourceName }, loader);
		resource.setCharset("UTF-8");
		resource.refresh();
		assertEquals("中文应用", resource.getString("app.name"));
		ClassPathResource empty = new ClassPathResource();
		empty.refresh();
		assertTrue(empty.toMap().isEmpty());
	}

	@Test
	public void resourceBundleResource_readsLocalizedBundles() throws Exception {
		ResourceBundleResource zh = new ResourceBundleResource(new String[] { "msg", "other" }, Locale.SIMPLIFIED_CHINESE, loader);
		zh.refresh();
		assertEquals("你好", zh.getString("greeting"));
		assertEquals("bye", zh.getString("farewell"));
		ResourceBundleResource root = new ResourceBundleResource(new String[] { "msg" }, Locale.ROOT, loader);
		root.refresh();
		assertEquals("hello", root.getString("greeting"));
		ResourceBundleResource none = new ResourceBundleResource((String[]) null, Locale.ROOT, loader);
		none.refresh();
		assertTrue(none.toMap().isEmpty());
		assertThrows(MissingResourceException.class, () -> new ResourceBundleResource("no-such-bundle", Locale.ROOT).refresh());
	}

	@Test
	public void realtimeResource_delegatesAndRefreshes() throws Exception {
		File file = write("rt.properties", CONTENT, StandardCharsets.UTF_8);
		RealtimeRefreshingResource resource = (RealtimeRefreshingResource) Resources.openFromFileSystem(file, 60);
		assertEquals("sa", resource.getString("db.user"));
		assertEquals("d", resource.getString("none", "d"));
		assertEquals(Integer.valueOf(8080), resource.getInteger("app.port"));
		assertEquals(Integer.valueOf(1), resource.getInteger("none", 1));
		assertEquals(Long.valueOf(8080), resource.getLong("app.port"));
		assertEquals(Long.valueOf(1), resource.getLong("none", 1L));
		assertEquals(Short.valueOf((short) 8080), resource.getShort("app.port"));
		assertEquals(Short.valueOf((short) 1), resource.getShort("none", (short) 1));
		assertEquals(Byte.valueOf((byte) 3), resource.getByte("app.small"));
		assertEquals(Byte.valueOf((byte) 1), resource.getByte("none", (byte) 1));
		assertEquals(Float.valueOf(0.5f), resource.getFloat("app.ratio"));
		assertEquals(Float.valueOf(1f), resource.getFloat("none", 1f));
		assertEquals(Double.valueOf(0.5), resource.getDouble("app.ratio"));
		assertEquals(Double.valueOf(1), resource.getDouble("none", 1d));
		assertEquals(Boolean.TRUE, resource.getBoolean("app.enabled"));
		assertEquals(Boolean.FALSE, resource.getBoolean("none", false));
		assertEquals(7, resource.toMap().size());
		assertEquals(5, resource.toMap("app", MatchMode.START).size());
		assertTrue(resource.toString().contains("db.url"));

		write("rt.properties", "db.user=root\n", StandardCharsets.UTF_8);
		assertTrue(resource.execute());
		assertEquals("root", resource.getString("db.user"));
		assertTrue(file.delete());
		assertFalse(resource.execute());
		resource.start();
		resource.start();
		resource.close();
		resource.close();
	}

	// ---------------------------------------------------------------- Resources and ResourceUtils

	@Test
	public void resources_openFromFileSystemAndClassPath() throws Exception {
		File file = write("open.properties", "k=v\n", StandardCharsets.UTF_8);
		assertEquals("v", Resources.openFromFileSystem(file).getString("k"));
		assertTrue(Resources.openFromClassPath("no-such-" + resourceName).toMap().isEmpty());
		Resource realtime = Resources.openFromClassPath("no-such-" + resourceName, 60);
		assertTrue(realtime instanceof RealtimeRefreshingResource);
		assertThrows(MissingResourceException.class, () -> Resources.openBundle("no-such-bundle", Locale.ROOT));
		assertThrows(MissingResourceException.class, () -> Resources.openBundle("no-such-bundle", Locale.ROOT, 60));
	}

	@Test
	public void resourceUtils_usesGivenClassLoader() throws Exception {
		Map<String, String> all = ResourceUtils.getResource(resourceName, loader);
		assertEquals("sa", all.get("db.user"));
		assertEquals(2, ResourceUtils.getResource(resourceName, loader, "db.").size());
		assertEquals(1, ResourceUtils.getResource(resourceName, loader, "url", MatchMode.END).size());
		try (URLClassLoader other = new URLClassLoader(new URL[] { new File(tempDir, "cp2").toURI().toURL() }, null)) {
			assertEquals("other", ResourceUtils.getResource(resourceName, other).get("db.user"));
		}
		assertEquals("sa", ResourceUtils.getResource(resourceName, loader).get("db.user"));
	}

	@Test
	public void resourceUtils_defaultLoaderReturnsEmptyForMissingResource() throws Exception {
		assertTrue(ResourceUtils.getResource(resourceName).isEmpty());
		assertTrue(ResourceUtils.getResource(resourceName, "db").isEmpty());
		assertTrue(ResourceUtils.getResource(resourceName, "db", MatchMode.START).isEmpty());
	}
}
