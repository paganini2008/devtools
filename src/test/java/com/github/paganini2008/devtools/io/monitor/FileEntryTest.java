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
package com.github.paganini2008.devtools.io.monitor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * FileEntryTest
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class FileEntryTest {

	@TempDir
	File root;

	@Test
	public void testRefresh() throws IOException {
		File file = new File(root, "a.txt");
		Files.write(file.toPath(), new byte[10]);
		FileEntry rootEntry = new FileEntry(root);
		rootEntry.refresh();
		assertTrue(rootEntry.isExists());
		assertTrue(rootEntry.isDirectory());
		assertEquals(0, rootEntry.getLength());
		assertEquals(0, rootEntry.getDepth());
		assertNull(rootEntry.getParent());
		assertSame(root, rootEntry.getFile());

		FileEntry child = rootEntry.newChildEntry(file);
		assertSame(rootEntry, child.getParent());
		assertEquals(1, child.getDepth());
		assertEquals("a.txt", child.getName());
		assertTrue(child.isExists());
		assertFalse(child.isDirectory());
		assertFalse(child.isHidden());
		assertEquals(10, child.getLength());
		assertEquals(file.lastModified(), child.getLastModified());
		assertTrue(child.toString().contains("name=a.txt"));

		FileEntry missing = new FileEntry(new File(root, "missing"));
		missing.refresh();
		assertFalse(missing.isExists());
		assertFalse(missing.isDirectory());
		assertEquals(0, missing.getLastModified());
		assertEquals(0, missing.getLength());
	}

	@Test
	public void testCompareTo() throws IOException {
		File file = new File(root, "a.txt");
		Files.write(file.toPath(), new byte[10]);
		FileEntry first = new FileEntry(file);
		first.refresh();
		FileEntry second = new FileEntry(file);
		second.refresh();
		assertFalse(first.compareTo(second));

		Files.write(file.toPath(), new byte[20]);
		second.refresh();
		assertTrue(first.compareTo(second));

		// existence changes are not reported as modifications
		FileEntry missing = new FileEntry(new File(root, "missing"));
		missing.refresh();
		assertFalse(first.compareTo(missing));
	}

	@Test
	public void testSetters() {
		FileEntry entry = new FileEntry(root);
		entry.setName("x");
		entry.setExists(true);
		entry.setDirectory(true);
		entry.setHidden(true);
		entry.setLastModified(123L);
		entry.setLength(456L);
		entry.setChildEntries(Collections.<String, FileEntry>emptyMap());
		assertEquals("x", entry.getName());
		assertTrue(entry.isExists());
		assertTrue(entry.isDirectory());
		assertTrue(entry.isHidden());
		assertEquals(123L, entry.getLastModified());
		assertEquals(456L, entry.getLength());
		assertTrue(entry.getChildEntries().isEmpty());
	}

}
