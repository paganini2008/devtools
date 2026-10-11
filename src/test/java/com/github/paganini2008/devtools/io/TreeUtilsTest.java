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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.github.paganini2008.devtools.MatchMode;

/**
 * TreeUtilsTest
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class TreeUtilsTest {

	private static final String NL = System.getProperty("line.separator");

	@TempDir
	File tempDir;

	private File root;

	@BeforeEach
	public void setUp() throws IOException {
		root = new File(tempDir, "root");
		touch("a.txt");
		touch(".hidden.txt");
		touch("sub/b.log");
		touch("sub/deep/c.txt");
		touch(".hiddendir/h.txt");
	}

	private void touch(String path) throws IOException {
		File file = new File(root, path);
		file.getParentFile().mkdirs();
		Files.write(file.toPath(), new byte[] { 'x' });
	}

	private static Set<String> set(String... values) {
		return new HashSet<String>(Arrays.asList(values));
	}

	private static Set<String> set(String[] values, boolean ignore) {
		return new HashSet<String>(Arrays.asList(values));
	}

	@Test
	public void scan_directoriesOnlyUpToMaxDepth() throws IOException {
		assertArrayEquals(new String[] { "|--- sub" + NL }, TreeUtils.scan(root, 0));
		assertEquals(set("|--- sub" + NL, "|    |--- deep" + NL), set(TreeUtils.scan(root, 5), true));
		assertEquals(0, TreeUtils.scan(root, -1).length);
	}

	@Test
	public void scan_includesFilesAndSkipsHiddenEntriesByDefault() throws IOException {
		String[] lines = TreeUtils.scan(root, 5, false, false);
		assertEquals(set("|--- sub" + NL, "|    |--- deep" + NL, "|--- a.txt" + NL, "|    |--- b.log" + NL, "|    |    |--- c.txt" + NL),
				set(lines, true));
	}

	@Test
	public void scan_includesHiddenEntriesWhenRequested() throws IOException {
		Set<String> lines = set(TreeUtils.scan(root, 5, true, false), true);
		assertTrue(lines.contains("|--- .hidden.txt" + NL));
		assertTrue(lines.contains("|--- .hiddendir" + NL));
		assertTrue(lines.contains("|    |--- h.txt" + NL));
		assertEquals(8, lines.size());
	}

	@Test
	public void scan_withCustomMatcher() throws IOException {
		TreeMatcher matcher = new TreeMatcher() {

			@Override
			public boolean matchDirectory(File directory, int depth, boolean hasLast) {
				return !directory.getName().equals("deep");
			}

			@Override
			public String getText(File directory, int depth, boolean hasLast) {
				return "D" + depth + ":" + directory.getName() + (hasLast ? "!" : "");
			}

			@Override
			public String getText(File directory, int depth, File file, boolean hasLast) {
				return "F" + depth + ":" + file.getName() + (hasLast ? "!" : "");
			}
		};
		assertEquals(set("D0:sub", "F0:a.txt", "F1:b.log!"), set(TreeUtils.scan(root, 1, false, matcher), true));
	}

	@Test
	public void treeMatcher_defaultsAcceptEverything() {
		TreeMatcher matcher = new TreeMatcher() {
			@Override
			public String getText(File directory, int depth, boolean hasLast) {
				return null;
			}

			@Override
			public String getText(File directory, int depth, File file, boolean hasLast) {
				return null;
			}
		};
		assertTrue(matcher.matchDirectory(root, 0, false));
		assertTrue(matcher.matchFile(root, 0, root, false));
	}

	@Test
	public void directoryTreeMatcher_includesAndExcludesByPath() throws IOException {
		DirectoryTreeMatcher onlyDeep = new DirectoryTreeMatcher("deep", null, MatchMode.END);
		assertFalse(onlyDeep.matchDirectory(new File(root, "sub"), 0, false));
		assertTrue(onlyDeep.matchDirectory(new File(root, "sub/deep"), 1, false));
		assertFalse(onlyDeep.matchFile(root, 0, new File(root, "a.txt"), false));

		DirectoryTreeMatcher noDeep = new DirectoryTreeMatcher(null, "deep", MatchMode.END);
		assertTrue(noDeep.matchDirectory(new File(root, "sub"), 0, false));
		assertFalse(noDeep.matchDirectory(new File(root, "sub/deep"), 1, false));

		DirectoryTreeMatcher multi = new DirectoryTreeMatcher(new String[] { "x", "sub" }, new String[] { "y" }, MatchMode.ANY_WHERE);
		assertTrue(multi.matchDirectory(new File(root, "sub"), 0, false));
		assertFalse(multi.matchDirectory(new File(root, ".hiddendir"), 0, false));

		assertArrayEquals(new String[] { "|--- sub" + NL }, TreeUtils.scan(root, 5, false, noDeep));
	}

	@Test
	public void fileTreeMatcher_includesAndExcludesByName() throws IOException {
		FileTreeMatcher txt = new FileTreeMatcher(".txt", null, MatchMode.END);
		assertTrue(txt.matchFile(root, 0, new File(root, "a.txt"), false));
		assertFalse(txt.matchFile(root, 0, new File(root, "b.log"), false));
		assertTrue(txt.matchDirectory(root, 0, false));

		FileTreeMatcher noA = new FileTreeMatcher(new String[] { "txt", "log" }, new String[] { "a." }, MatchMode.ANY_WHERE);
		assertFalse(noA.matchFile(root, 0, new File(root, "a.txt"), false));
		assertTrue(noA.matchFile(root, 0, new File(root, "b.log"), false));

		FileTreeMatcher all = new FileTreeMatcher();
		assertTrue(all.matchFile(root, 0, new File(root, "anything"), false));

		Set<String> lines = set(TreeUtils.scan(root, 5, false, txt), true);
		assertTrue(lines.contains("|--- a.txt" + NL));
		assertFalse(lines.contains("|    |--- b.log" + NL));
	}

	@Test
	public void scanAndSave_writesTreeToStreamAndFile() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		TreeUtils.scanAndSave(root, 0, out);
		assertEquals("|--- sub" + NL, new String(out.toByteArray(), Charset.defaultCharset()));

		File file = new File(tempDir, "out/tree.txt");
		TreeUtils.scanAndSave(root, 0, file);
		assertEquals("|--- sub" + NL, new String(Files.readAllBytes(file.toPath()), Charset.defaultCharset()));

		TreeUtils.scanAndSave(root, 0, false, false, file);
		String content = new String(Files.readAllBytes(file.toPath()), Charset.defaultCharset());
		assertTrue(content.contains("|--- a.txt" + NL));

		out = new ByteArrayOutputStream();
		TreeUtils.scanAndSave(root, 0, false, new FileTreeMatcher(), out);
		assertTrue(new String(out.toByteArray(), Charset.defaultCharset()).contains("a.txt"));

		TreeUtils.scanAndSave(root, 0, false, new DirectoryTreeMatcher(), file);
		assertEquals("|--- sub" + NL, new String(Files.readAllBytes(file.toPath()), Charset.defaultCharset()));
	}
}
