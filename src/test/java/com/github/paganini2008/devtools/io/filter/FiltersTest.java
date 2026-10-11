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
package com.github.paganini2008.devtools.io.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.github.paganini2008.devtools.MatchMode;
import com.github.paganini2008.devtools.io.SizeUnit;

/**
 * FiltersTest
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class FiltersTest {

	@TempDir
	File root;

	private File txt;
	private File upperTxt;
	private File log;
	private File big;
	private File emptyDir;
	private File fullDir;

	@BeforeEach
	public void setUp() throws IOException {
		txt = write("readme.txt", 10);
		upperTxt = write("NOTES.TXT", 0);
		log = write("app.log", 100);
		big = write("data.bin", 2048);
		emptyDir = new File(root, "empty");
		assertTrue(emptyDir.mkdir());
		fullDir = new File(root, "full");
		assertTrue(fullDir.mkdir());
		assertTrue(new File(fullDir, "a.txt").createNewFile());
		assertTrue(new File(fullDir, "b.txt").createNewFile());
	}

	private File write(String name, int size) throws IOException {
		File file = new File(root, name);
		byte[] data = new byte[size];
		Arrays.fill(data, (byte) 'x');
		Files.write(file.toPath(), data);
		return file;
	}

	private Set<String> list(java.io.FileFilter filter) {
		Set<String> names = new HashSet<String>();
		for (File f : root.listFiles(filter)) {
			names.add(f.getName());
		}
		return names;
	}

	private Set<String> listByName(java.io.FilenameFilter filter) {
		return new HashSet<String>(Arrays.asList(root.list(filter)));
	}

	private static Set<String> set(String... names) {
		return new HashSet<String>(Arrays.asList(names));
	}

	@Test
	public void testFileAndDirectory() {
		assertEquals(set("readme.txt", "NOTES.TXT", "app.log", "data.bin"), list(Filters.isFile()));
		assertEquals(set("empty", "full"), list(Filters.isDirectory()));
		assertEquals(list(Filters.isFile()), listByName(Filters.isFile()));
		assertEquals(set("empty", "full"), listByName(Filters.isDirectory()));
	}

	@Test
	public void testHidden() throws IOException {
		File hidden = write(".hidden", 1);
		LogicalFileFilter filter = Filters.isHidden();
		assertEquals(hidden.isHidden(), filter.accept(hidden));
		assertFalse(filter.accept(txt));
	}

	@Test
	public void testExtension() {
		assertEquals(set("readme.txt", "NOTES.TXT"), list(Filters.findByExtension("txt", true)));
		assertEquals(set("readme.txt"), list(Filters.findByExtension("txt", false)));
		assertEquals(set("readme.txt", "NOTES.TXT"), listByName(new ExtensionFileFilter(".txt")));
		// directories never match even when their name has the extension
		File dir = new File(root, "folder.txt");
		assertTrue(dir.mkdir());
		assertFalse(new ExtensionFileFilter("txt").accept(dir));
		assertFalse(new ExtensionFileFilter("txt").accept(root, "missing.txt"));
		assertThrows(IllegalArgumentException.class, () -> new ExtensionFileFilter(null));
	}

	@Test
	public void testName() {
		assertEquals(set("readme.txt"), list(Filters.findByName("README", true)));
		assertTrue(list(Filters.findByName("README", false)).isEmpty());
		assertEquals(set("readme.txt"), listByName(Filters.findByName("readme", false)));
		assertEquals(set("readme.txt"), list(Filters.findByName("read", MatchMode.START)));
		assertEquals(set("app.log"), list(Filters.findByName("pp", MatchMode.ANY_WHERE)));
		assertEquals(set("app.log", "data.bin"), list(Filters.findByName("[ad][a-z]+", MatchMode.REGEX)));
	}

	@Test
	public void testLength() {
		assertEquals(set("NOTES.TXT"), list(LengthFileFilter.eq(0, SizeUnit.BYTES).and(Filters.isFile())));
		assertEquals(set("data.bin"), list(LengthFileFilter.eq(2, SizeUnit.KB)));
		assertEquals(set("data.bin"), list(LengthFileFilter.gt(1, SizeUnit.KB)));
		assertEquals(set("data.bin"), list(LengthFileFilter.gte(2, SizeUnit.KB)));
		assertEquals(set("readme.txt", "NOTES.TXT"), list(LengthFileFilter.lt(100, SizeUnit.BYTES).and(Filters.isFile())));
		assertEquals(set("readme.txt", "NOTES.TXT", "app.log"), list(LengthFileFilter.lte(100, SizeUnit.BYTES).and(Filters.isFile())));
		assertEquals(set("readme.txt", "NOTES.TXT", "app.log"), list(LengthFileFilter.ne(2048, SizeUnit.BYTES).and(Filters.isFile())));
		assertEquals(set("app.log"), list(Filters.findByLength(100, SizeUnit.BYTES, Operator.EQ)));
	}

	@Test
	public void testLastModified() {
		long time = 1500000000000L;
		assertTrue(txt.setLastModified(time));
		assertTrue(log.setLastModified(time + 10000));
		assertTrue(LastModifiedFileFilter.eq(txt.lastModified()).accept(txt));
		assertFalse(LastModifiedFileFilter.ne(txt.lastModified()).accept(txt));
		assertTrue(LastModifiedFileFilter.lt(log.lastModified()).accept(txt));
		assertTrue(LastModifiedFileFilter.lte(txt.lastModified()).accept(txt));
		assertTrue(LastModifiedFileFilter.gt(txt.lastModified()).accept(log));
		assertTrue(LastModifiedFileFilter.gte(log.lastModified()).accept(log));
		assertFalse(LastModifiedFileFilter.gt(log.lastModified()).accept(txt));
		assertTrue(Filters.findByLastModified(time + 5000, Operator.LT).accept(root, "readme.txt"));
	}

	@Test
	public void testSubfolders() {
		assertTrue(FileSizeFileFilter.eq(0).accept(emptyDir));
		assertTrue(FileSizeFileFilter.eq(2).accept(fullDir));
		assertTrue(FileSizeFileFilter.ne(2).accept(emptyDir));
		assertTrue(FileSizeFileFilter.gt(1).accept(fullDir));
		assertTrue(FileSizeFileFilter.gte(2).accept(fullDir));
		assertTrue(FileSizeFileFilter.lt(1).accept(emptyDir));
		assertTrue(FileSizeFileFilter.lte(0).accept(emptyDir));
		assertFalse(FileSizeFileFilter.lt(2).accept(fullDir));
		// a regular file has no children
		assertTrue(Filters.hasSubfolders(0, Operator.EQ).accept(txt));
	}

	@Test
	public void testTrueFalse() {
		assertTrue(TrueFileFilter.INSTANCE.accept(txt));
		assertTrue(TrueFileFilter.INSTANCE.accept(root, "anything"));
		assertFalse(TrueFileFilter.INSTANCE.accept(null));
		assertFalse(FalseFileFilter.INSTANCE.accept(txt));
		assertFalse(FalseFileFilter.INSTANCE.accept(root, "readme.txt"));
		assertTrue(FalseFileFilter.INSTANCE instanceof FalseFileFilter);
	}

	@Test
	public void testLogical() {
		LogicalFileFilter txtFiles = Filters.findByExtension("txt", true);
		LogicalFileFilter logFiles = Filters.findByExtension("log", true);
		assertEquals(set("readme.txt", "NOTES.TXT", "app.log"), list(txtFiles.or(logFiles)));
		assertEquals(set("readme.txt", "NOTES.TXT", "app.log"), listByName(txtFiles.or(logFiles)));
		assertEquals(set("readme.txt"), list(txtFiles.and(Filters.findByName("readme", true))));
		assertEquals(set("readme.txt"), listByName(txtFiles.and(Filters.findByName("readme", true))));
		assertEquals(set("app.log", "data.bin", "empty", "full"), list(txtFiles.not()));
		assertEquals(set("app.log", "data.bin", "empty", "full"), listByName(txtFiles.not()));
	}

	@Test
	public void testCreate() {
		LogicalFileFilter and = AndFileFilter.create(Filters.isFile(), Filters.findByExtension("txt", true),
				LengthFileFilter.gt(0, SizeUnit.BYTES));
		assertEquals(set("readme.txt"), list(and));
		LogicalFileFilter or = OrFileFilter.create(Filters.findByExtension("log", true), Filters.findByExtension("bin", true),
				Filters.isDirectory());
		assertEquals(set("app.log", "data.bin", "empty", "full"), list(or));
		assertEquals(set("app.log"), list(OrFileFilter.create(Filters.findByExtension("log", true))));

		// no filters: null is returned (kept for backward compatibility)
		assertNull(AndFileFilter.create());
		assertNull(OrFileFilter.create());
		assertNull(AndFileFilter.create((FileFilter[]) null));
		assertNull(OrFileFilter.create((FileFilter[]) null));
	}

	@Test
	public void testLengthAfterRewrite() throws IOException {
		Files.write(txt.toPath(), "hello".getBytes(StandardCharsets.UTF_8));
		assertTrue(LengthFileFilter.eq(5, SizeUnit.BYTES).accept(txt));
		assertFalse(LengthFileFilter.eq(5, SizeUnit.BYTES).accept(upperTxt));
		assertTrue(LengthFileFilter.gt(5, SizeUnit.BYTES).accept(big));
	}

}
