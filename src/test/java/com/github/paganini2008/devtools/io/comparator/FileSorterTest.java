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
package com.github.paganini2008.devtools.io.comparator;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * FileSorterTest
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class FileSorterTest {

	@TempDir
	File root;

	private File file(String name, int size) throws IOException {
		File file = new File(root, name);
		Files.write(file.toPath(), new byte[size]);
		return file;
	}

	private File dir(String name, int children) throws IOException {
		File dir = new File(root, name);
		assertTrue(dir.mkdirs());
		for (int i = 0; i < children; i++) {
			Files.write(new File(dir, "child" + i + ".dat").toPath(), new byte[100]);
		}
		return dir;
	}

	@Test
	public void testDirectoriesFirst() throws IOException {
		File f = file("a.txt", 1);
		File d = dir("z", 0);
		File[] result = FileSorter.sort(new File[] { f, d });
		assertArrayEquals(new File[] { d, f }, result);
		assertEquals(0, new FileComparator().compare(f, file("b.txt", 1)));
		assertNull(FileSorter.sort(null));
		assertEquals(0, FileSorter.sort(new File[0]).length);
	}

	@Test
	public void testSortByName() throws IOException {
		File b = file("b.txt", 1);
		File a = file("A.log", 1);
		File c = file("c.bin", 1);
		File d = dir("dir", 0);
		assertArrayEquals(new File[] { d, a, b, c }, FileSorter.sortByName(new File[] { c, b, d, a }));
		// case sensitive: upper case letters sort before lower case ones
		FileNameComparator caseSensitive = new FileNameComparator(false);
		assertTrue(caseSensitive.compare(a, b) < 0);
		File upperB = file("B.dat", 1);
		assertTrue(caseSensitive.compare(upperB, a) > 0);
		assertTrue(caseSensitive.compare(upperB, b) < 0);
		assertEquals(0, new FileNameComparator().compare(upperB, b));
	}

	@Test
	public void testSortByExtension() throws IOException {
		File txt = file("a.txt", 1);
		File bin = file("b.bin", 1);
		File log = file("c.LOG", 1);
		File none = file("noext", 1);
		assertArrayEquals(new File[] { none, bin, log, txt }, FileSorter.sortByExtension(new File[] { txt, log, none, bin }));
		FileExtensionComparator caseSensitive = new FileExtensionComparator(false);
		assertTrue(caseSensitive.compare(log, bin) < 0);
	}

	@Test
	public void testSortByLength() throws IOException {
		File small = file("small", 1);
		File medium = file("medium", 50);
		File large = file("large", 500);
		assertArrayEquals(new File[] { small, medium, large }, FileSorter.sortByLength(new File[] { large, small, medium }));
	}

	@Test
	public void testSortByLastModified() throws IOException {
		File older = file("older", 1);
		File newer = file("newer", 1);
		assertTrue(older.setLastModified(1500000000000L));
		assertTrue(newer.setLastModified(1600000000000L));
		assertArrayEquals(new File[] { older, newer }, FileSorter.sortByLastModified(new File[] { newer, older }));
	}

	@Test
	public void testSortByPath() throws IOException {
		File b = file("b", 1);
		File a = file("a", 1);
		File sub = dir("sub", 0);
		File inner = new File(sub, "x");
		assertTrue(inner.createNewFile());
		assertArrayEquals(new File[] { sub, a, b, inner }, FileSorter.sortByPath(new File[] { inner, b, sub, a }));
		assertTrue(new FilePathComparator(false).compare(new File(root, "A"), a) < 0);
	}

	@Test
	public void testSortByChildrenCount() throws IOException {
		File many = dir("many", 3);
		File few = dir("few", 1);
		File none = dir("none", 0);
		File plain = file("plain", 1);
		assertArrayEquals(new File[] { none, few, many, plain },
				FileSorter.sortByChildrenCount(new File[] { plain, many, none, few }));
	}

	@Test
	public void testSortBySize() throws IOException {
		File bigDir = dir("bigDir", 3);
		File smallDir = dir("smallDir", 1);
		File bigFile = file("bigFile", 1000);
		File smallFile = file("smallFile", 10);
		assertArrayEquals(new File[] { smallDir, bigDir, smallFile, bigFile },
				FileSorter.sortBySize(new File[] { bigFile, bigDir, smallFile, smallDir }));

		List<File> list = new ArrayList<File>(Arrays.asList(bigFile, smallFile));
		new FileSizeComparator(pathname -> pathname.getName().startsWith("child0")).sort(list);
		assertEquals(Arrays.asList(smallFile, bigFile), list);
		// with a filter only "child0.dat" is counted, so both directories have the same size
		assertEquals(0, new FileSizeComparator(pathname -> pathname.getName().startsWith("child0")).compare(bigDir, smallDir));
	}

	@Test
	public void testSortBySizeMissingFile() throws IOException {
		File exists = file("exists", 1);
		File missing = new File(root, "missing");
		assertThrows(IllegalStateException.class, () -> new FileSizeComparator().compare(exists, missing));
	}

	@Test
	public void testReverse() throws IOException {
		File small = file("small", 1);
		File large = file("large", 500);
		File[] files = new File[] { small, large };
		Arrays.sort(files, new FileLengthComparator().reverse());
		assertArrayEquals(new File[] { large, small }, files);
	}

}
