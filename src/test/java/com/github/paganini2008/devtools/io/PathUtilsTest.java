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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.File;

import org.junit.jupiter.api.Test;

/**
 * PathUtilsTest
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class PathUtilsTest {

	@Test
	public void osDetection_matchesFileSeparator() {
		assertEquals(File.separatorChar == '/', PathUtils.isUnixOS());
		assertEquals(File.separatorChar == '\\', PathUtils.isWindowsOS());
		assertNotEquals(PathUtils.isUnixOS(), PathUtils.isWindowsOS());
		assertEquals(".", PathUtils.EXTENSION_SEPARATOR);
		assertEquals(String.valueOf(File.separatorChar), PathUtils.SYSTEM_SEPARATOR);
	}

	@Test
	public void runRootPath_returnsClassPathRootWithoutProtocol() {
		String path = PathUtils.runRootPath();
		assertNotNull(path);
		assertFalse(path.startsWith("file:"));
	}

	@Test
	public void getBaseName_stripsDirectoryAndExtension() {
		assertEquals("c", PathUtils.getBaseName("a/b/c.txt"));
		assertEquals("c", PathUtils.getBaseName("a\\b\\c.txt"));
		assertEquals("c", PathUtils.getBaseName("c"));
		assertEquals("c", PathUtils.getBaseName("a.b/c"));
		assertEquals("archive.tar", PathUtils.getBaseName("archive.tar.gz"));
		assertEquals("", PathUtils.getBaseName(".bashrc"));
		assertEquals("", PathUtils.getBaseName(""));
		assertEquals("", PathUtils.getBaseName(null));
	}

	@Test
	public void getName_returnsLastPathSegment() {
		assertEquals("c.txt", PathUtils.getName("a/b/c.txt"));
		assertEquals("c.txt", PathUtils.getName("a\\b\\c.txt"));
		assertEquals("c.txt", PathUtils.getName("c.txt"));
		assertEquals("c", PathUtils.getName("/c"));
		assertEquals("", PathUtils.getName("a/b/"));
		assertEquals("", PathUtils.getName(" "));
		assertEquals("", PathUtils.getName(null));
	}

	@Test
	public void getExtension_returnsTextAfterLastDotOfFileName() {
		assertEquals("txt", PathUtils.getExtension("a/b.txt"));
		assertEquals("gz", PathUtils.getExtension("archive.tar.gz"));
		assertEquals("", PathUtils.getExtension("a.b/c"));
		assertEquals("", PathUtils.getExtension("noext"));
		assertEquals("", PathUtils.getExtension(".bashrc"));
		assertEquals("bashrc", PathUtils.getExtension("home/.bashrc"));
		assertEquals("", PathUtils.getExtension(null));
	}

	@Test
	public void format_collapsesSeparatorsAndTrimsLeadingAndTrailingSeparator() {
		assumeTrue(PathUtils.isUnixOS());
		assertEquals("a/b/c", PathUtils.format("//a\\\\b//c/"));
		assertEquals("a|b", PathUtils.format("a|b"));
		assertEquals("", PathUtils.format("/"));
		assertEquals("", PathUtils.format("\\\\"));
		assertEquals("", PathUtils.format(""));
		assertEquals("", PathUtils.format(null));
	}

	@Test
	public void getProjectPath_isFormattedWorkingDirectory() {
		assumeTrue(PathUtils.isUnixOS());
		assertEquals(new File("").getAbsolutePath(), PathUtils.getProjectPath());
		assertTrue(PathUtils.getProjectPath().startsWith("/"));
	}

	@Test
	public void indexOfLastSeparator_handlesBothSeparators() {
		assertEquals(3, PathUtils.indexOfLastSeparator("a/b\\c"));
		assertEquals(1, PathUtils.indexOfLastSeparator("a\\bc"));
		assertEquals(-1, PathUtils.indexOfLastSeparator("abc"));
		assertEquals(-1, PathUtils.indexOfLastSeparator(null));
	}

	@Test
	public void indexOfExtension_ignoresDotsInDirectories() {
		assertEquals(3, PathUtils.indexOfExtension("a/b.c"));
		assertEquals(-1, PathUtils.indexOfExtension("a.b/c"));
		assertEquals(-1, PathUtils.indexOfExtension(""));
	}

	@Test
	public void indexOfSeparator_returnsFirstSeparatorOfEitherKind() {
		assertEquals(1, PathUtils.indexOfSeparator("a\\b/c"));
		assertEquals(1, PathUtils.indexOfSeparator("a/b\\c"));
		assertEquals(2, PathUtils.indexOfSeparator("ab/c"));
		assertEquals(2, PathUtils.indexOfSeparator("ab\\c"));
		assertEquals(-1, PathUtils.indexOfSeparator("abc"));
		assertEquals(-1, PathUtils.indexOfSeparator(null));
	}

	@Test
	public void getParent_returnsDirectoryPart() {
		assumeTrue(PathUtils.isUnixOS());
		assertEquals("a/b", PathUtils.getParent("a/b/c.txt"));
		assertEquals("a.b", PathUtils.getParent("a.b/c"));
		assertEquals("a/b", PathUtils.getParent("a/b/"));
		assertEquals("", PathUtils.getParent("c.txt"));
		assertEquals("/", PathUtils.getParent("/c"));
		assertEquals("/data", PathUtils.getParent("/data/report.final.pdf"));
		assertEquals("/a/b", PathUtils.getParent("//a//b/c"));
		assertEquals("/a", PathUtils.getParent("\\a\\b"));
		assertTrue(PathUtils.getParent("x\\y\\z").endsWith("y"));
		assertThrows(IllegalArgumentException.class, () -> PathUtils.getParent(""));
		assertThrows(IllegalArgumentException.class, () -> PathUtils.getParent(null));
	}
}
