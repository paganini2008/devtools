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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * ClassFinderTest: tests for ClassFinder and ClassFileFinder using an isolated context class loader.
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class ClassFinderTest {

	@TempDir
	File tempDir;

	private ClassLoader original;
	private URLClassLoader loader;

	@BeforeEach
	public void setUp() {
		original = Thread.currentThread().getContextClassLoader();
	}

	@AfterEach
	public void tearDown() throws IOException {
		Thread.currentThread().setContextClassLoader(original);
		if (loader != null) {
			loader.close();
		}
	}

	private File createClasses(String rootName) throws IOException {
		File root = new File(tempDir, rootName);
		for (String path : Arrays.asList("pkg/a/Foo.class", "pkg/a/Foo$1.class", "pkg/a/Foo$Inner.class", "pkg/a/FooBar.class",
				"pkg/a/readme.txt", "pkg/a/sub/Baz.class", "pkg/a/sub/deeper/Q.class", "pkg/ab/Other.class")) {
			File file = new File(root, path);
			file.getParentFile().mkdirs();
			Files.write(file.toPath(), new byte[] { 0 });
		}
		return root;
	}

	private void useClassPath(File... entries) throws IOException {
		URL[] urls = new URL[entries.length];
		for (int i = 0; i < entries.length; i++) {
			urls[i] = entries[i].toURI().toURL();
		}
		loader = new URLClassLoader(urls, null);
		Thread.currentThread().setContextClassLoader(loader);
	}

	private File createJar() throws IOException {
		File jar = new File(tempDir, "lib dir/classes.jar");
		jar.getParentFile().mkdirs();
		try (JarOutputStream out = new JarOutputStream(new FileOutputStream(jar))) {
			for (String name : Arrays.asList("pkg/", "pkg/a/", "pkg/a/Foo.class", "pkg/a/Foo$1.class", "pkg/a/notes.txt", "pkg/a/sub/",
					"pkg/a/sub/Baz.class", "pkg/ab/", "pkg/ab/Other.class", "Top.class")) {
				out.putNextEntry(new JarEntry(name));
				if (!name.endsWith("/")) {
					out.write(0);
				}
				out.closeEntry();
			}
		}
		return jar;
	}

	private static Map<String, String> lookup(String packageName, boolean recursive) throws IOException {
		final Map<String, String> result = new TreeMap<String, String>();
		ClassFinder.lookup(packageName, recursive, (fileName, className) -> result.put(className, fileName));
		return result;
	}

	@Test
	public void lookup_projectDirectorySkipsAnonymousClasses() throws IOException {
		useClassPath(createClasses("with space"));
		Map<String, String> result = lookup("pkg.a", false);
		assertEquals(Arrays.asList("pkg.a.Foo", "pkg.a.Foo$Inner", "pkg.a.FooBar"), Arrays.asList(result.keySet().toArray()));
		assertEquals("/pkg/a/Foo.class", result.get("pkg.a.Foo"));
	}

	@Test
	public void lookup_projectDirectoryRecursive() throws IOException {
		useClassPath(createClasses("classes"));
		Map<String, String> result = lookup("pkg.a", true);
		assertTrue(result.containsKey("pkg.a.sub.Baz"));
		assertTrue(result.containsKey("pkg.a.sub.deeper.Q"));
		assertEquals("/pkg/a/sub/Baz.class", result.get("pkg.a.sub.Baz"));
		assertEquals(5, result.size());
	}

	@Test
	public void lookup_jarRespectsPackageBoundaries() throws IOException {
		useClassPath(createJar());
		Map<String, String> flat = lookup("pkg.a", false);
		assertEquals(Arrays.asList("pkg.a.Foo", "pkg.a.Foo$1"), Arrays.asList(flat.keySet().toArray()));
		assertEquals("pkg/a/Foo.class", flat.get("pkg.a.Foo"));
		Map<String, String> recursive = lookup("pkg.a", true);
		assertEquals(Arrays.asList("pkg.a.Foo", "pkg.a.Foo$1", "pkg.a.sub.Baz"), Arrays.asList(recursive.keySet().toArray()));
	}

	@Test
	public void lookup_toleratesNullHandlerAndMissingPackage() throws IOException {
		useClassPath(createClasses("c1"), createJar());
		ClassFinder.lookup("pkg.a", true, null);
		ClassFinder.lookup("pkg.ab", false, null);
		assertTrue(lookup("no.such.pkg", true).isEmpty());
		assertThrows(IllegalArgumentException.class, () -> ClassFinder.lookup(" ", true, null));
		assertThrows(IllegalArgumentException.class, () -> ClassFinder.lookup(null, true, null));
	}

	@Test
	public void lookup_fallsBackToOwnClassLoaderWithoutContextLoader() throws IOException {
		Thread.currentThread().setContextClassLoader(null);
		final Map<String, String> result = new TreeMap<String, String>();
		ClassFinder.lookup("com.github.paganini2008.devtools.io", false, (fileName, className) -> result.put(className, fileName));
		assertTrue(result.size() > 0);
		for (String className : result.keySet()) {
			assertTrue(className.startsWith("com.github.paganini2008.devtools.io."));
		}
	}

	// ---------------------------------------------------------------- ClassFileFinder

	private static class CollectingFinder extends ClassFileFinder {

		final Map<String, Integer> classes = new TreeMap<String, Integer>();

		CollectingFinder(String packageName) {
			super(packageName);
		}

		@Override
		protected void handleClass(File javaFile, String className, int depth) {
			assertTrue(javaFile.isFile());
			classes.put(className, depth);
		}
	}

	@Test
	public void classFileFinder_walksPackageWithDepth() throws IOException {
		useClassPath(createClasses("cf classes"));
		CollectingFinder finder = new CollectingFinder("pkg.a");
		assertEquals("pkg.a", finder.getPackageName());
		finder.walk();
		assertEquals(6, finder.classes.size());
		assertEquals(Integer.valueOf(0), finder.classes.get("pkg.a.Foo$1"));
		assertEquals(Integer.valueOf(1), finder.classes.get("pkg.a.sub.Baz"));
		assertEquals(Integer.valueOf(2), finder.classes.get("pkg.a.sub.deeper.Q"));

		CollectingFinder limited = new CollectingFinder("pkg.a");
		limited.setMaxDepth(0);
		limited.walk();
		assertTrue(limited.classes.containsKey("pkg.a.sub.Baz"));
		assertTrue(!limited.classes.containsKey("pkg.a.sub.deeper.Q"));
	}

	@Test
	public void classFileFinder_rejectsJarAndMissingPackages() throws IOException {
		useClassPath(createJar());
		assertThrows(IllegalArgumentException.class, () -> new CollectingFinder("pkg.a").walk());
		assertThrows(IllegalArgumentException.class, () -> new CollectingFinder("no.such").walk());
		assertThrows(IllegalArgumentException.class, () -> new CollectingFinder(null).walk());
	}

	@Test
	public void classFileFinder_fallsBackToOwnClassLoader() {
		Thread.currentThread().setContextClassLoader(null);
		CollectingFinder finder = new CollectingFinder("com.github.paganini2008.devtools.io");
		finder.setMaxDepth(0);
		finder.walk();
		assertTrue(finder.classes.size() > 0);
	}
}
