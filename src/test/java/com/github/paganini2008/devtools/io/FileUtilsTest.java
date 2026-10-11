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

import static com.github.paganini2008.devtools.io.IoTestSupport.assertNoFileHandleLeak;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * FileUtilsTest
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class FileUtilsTest {

	private static final String NL = IOUtils.NEWLINE;
	private static final Charset UTF8 = StandardCharsets.UTF_8;
	private static final Charset GBK = Charset.forName("GBK");
	private static final String CHINESE = "中文内容";

	@TempDir
	File tempDir;

	private File write(String name, String content, Charset charset) throws IOException {
		File file = new File(tempDir, name);
		file.getParentFile().mkdirs();
		Files.write(file.toPath(), content.getBytes(charset));
		return file;
	}

	private static String read(File file, Charset charset) throws IOException {
		return new String(Files.readAllBytes(file.toPath()), charset);
	}

	private static Set<String> relativePaths(File root) throws IOException {
		Set<String> paths = new HashSet<String>();
		collect(root, root, paths);
		return paths;
	}

	private static void collect(File root, File dir, Set<String> paths) {
		File[] files = dir.listFiles();
		if (files == null) {
			return;
		}
		for (File file : files) {
			String rel = root.toURI().relativize(file.toURI()).getPath();
			paths.add(rel);
			if (file.isDirectory()) {
				collect(root, file, paths);
			}
		}
	}

	private File createTree(String name) throws IOException {
		File root = new File(tempDir, name);
		write(name + "/a.txt", "A", UTF8);
		write(name + "/sub/b.txt", "BB", UTF8);
		write(name + "/sub/deep/c.txt", CHINESE, UTF8);
		new File(root, "empty").mkdirs();
		return root;
	}

	// ---------------------------------------------------------------- names and paths

	@Test
	public void getFile_joinsNames() {
		assertEquals(new File(new File(tempDir, "a"), "b"), FileUtils.getFile(tempDir, "a", "b"));
		assertEquals(tempDir, FileUtils.getFile(tempDir));
		assertEquals(new File(new File("a"), "b"), FileUtils.getFile("a", "b"));
		assertNull(FileUtils.getFile(new String[0]));
		assertThrows(IllegalArgumentException.class, () -> FileUtils.getFile((File) null, "a"));
		assertThrows(IllegalArgumentException.class, () -> FileUtils.getFile(tempDir, (String[]) null));
		assertThrows(IllegalArgumentException.class, () -> FileUtils.getFile((String[]) null));
	}

	@Test
	public void systemDirectories_followSystemProperties() {
		assertEquals(System.getProperty("java.io.tmpdir"), FileUtils.getTempDirectoryPath());
		assertEquals(new File(System.getProperty("java.io.tmpdir")), FileUtils.getTempDirectory());
		assertEquals(System.getProperty("user.home"), FileUtils.getUserHomePath());
		assertEquals(new File(System.getProperty("user.home")), FileUtils.getUserHome());
		assertEquals(System.getProperty("user.dir"), FileUtils.getUserDirectoryPath());
		assertEquals(new File(System.getProperty("user.dir")), FileUtils.getUserDirectory());
	}

	@Test
	public void getBaseNameAndExtension_areNullSafe() {
		assertEquals("report", FileUtils.getBaseName(new File("dir/report.txt")));
		assertEquals("txt", FileUtils.getExtension(new File("dir/report.txt")));
		assertEquals("", FileUtils.getBaseName(null));
		assertEquals("", FileUtils.getExtension(null));
	}

	@Test
	public void getFiles_mapsNamesAndKeepsNulls() {
		File[] files = FileUtils.getFiles(tempDir, "a", null);
		assertEquals(new File(tempDir, "a"), files[0]);
		assertNull(files[1]);
		assertNull(FileUtils.getFiles(tempDir, (String[]) null));
		files = FileUtils.getFiles("x", null);
		assertEquals(new File("x"), files[0]);
		assertNull(files[1]);
		assertNull(FileUtils.getFiles((String[]) null));
	}

	// ---------------------------------------------------------------- touch and mkdirs

	@Test
	public void touch_createsFileWithParentsOrUpdatesTimestamp() throws IOException {
		File file = new File(tempDir, "x/y/z.txt");
		FileUtils.touch(file);
		assertTrue(file.isFile());
		assertTrue(file.setLastModified(1000L));
		FileUtils.touch(file);
		assertTrue(file.lastModified() > 1000L);
		FileUtils.touch((File) null);
		FileUtils.touch(new File(tempDir, "s.txt").getAbsolutePath());
		assertTrue(new File(tempDir, "s.txt").isFile());
	}

	@Test
	public void touch_failsWhenParentIsAFile() throws IOException {
		File parent = write("parent", "", UTF8);
		assertThrows(IOException.class, () -> FileUtils.touch(new File(parent, "child")));
	}

	@Test
	public void mkdirs_returnsTrueWhenAlreadyExistingAndFalseWhenCreated() throws IOException {
		File dir = new File(tempDir, "m/n");
		assertFalse(FileUtils.mkdirs(dir));
		assertTrue(dir.isDirectory());
		assertTrue(FileUtils.mkdirs(dir));
		assertTrue(FileUtils.mkdirs((File) null));
		FileUtils.mkdirs(new File(tempDir, "k").getAbsolutePath());
		assertTrue(new File(tempDir, "k").isDirectory());
		File file = write("f", "", UTF8);
		assertThrows(IOException.class, () -> FileUtils.mkdirs(new File(file, "sub")));
	}

	// ---------------------------------------------------------------- opening streams

	@Test
	public void openInputStream_validatesFile() throws IOException {
		File file = write("in.txt", "abc", UTF8);
		try (FileInputStream in = FileUtils.openInputStream(file.getAbsolutePath())) {
			assertEquals("abc", IOUtils.toString(in, UTF8));
		}
		assertThrows(FileNotFoundException.class, () -> FileUtils.openInputStream(new File(tempDir, "missing")));
		assertThrows(IOException.class, () -> FileUtils.openInputStream(tempDir));
		assertThrows(IllegalArgumentException.class, () -> FileUtils.openInputStream((File) null));
	}

	@Test
	public void openInputStream_failsForUnreadableFile() throws IOException {
		File file = write("secret.txt", "abc", UTF8);
		assumeTrue(file.setReadable(false));
		try {
			assumeFalse(file.canRead(), "running with privileges that ignore permissions");
			IOException e = assertThrows(IOException.class, () -> FileUtils.openInputStream(file));
			assertTrue(e.getMessage().contains("can not be read"));
		} finally {
			file.setReadable(true);
		}
	}

	@Test
	public void getRandomAccessFile_opensExistingFile() throws IOException {
		File file = write("raf.txt", "abc", UTF8);
		try (RandomAccessFile raf = FileUtils.getRandomAccessFile(file)) {
			assertEquals(3, raf.length());
		}
		assertThrows(FileNotFoundException.class, () -> FileUtils.getRandomAccessFile(new File(tempDir, "nope")));
	}

	@Test
	public void readers_decodeWithCharset() throws IOException {
		File file = write("gbk.txt", CHINESE + "\n第二行", GBK);
		try (BufferedReader reader = FileUtils.getBufferedReader(file.getAbsolutePath(), "GBK")) {
			assertEquals(CHINESE, reader.readLine());
		}
		try (BufferedReader reader = FileUtils.getBufferedReader(file, "GBK", 4)) {
			assertEquals(CHINESE, reader.readLine());
		}
		try (LineNumberReader reader = FileUtils.getLineNumberReader(file, GBK)) {
			reader.readLine();
			assertEquals("第二行", reader.readLine());
			assertEquals(2, reader.getLineNumber());
		}
	}

	@Test
	public void getBufferedReader_invalidBufferSizeDoesNotLeakTheFile() throws Exception {
		File file = write("buf.txt", "x", StandardCharsets.UTF_8);
		assertNoFileHandleLeak(() -> {
			assertThrows(IllegalArgumentException.class, () -> FileUtils.getBufferedReader(file, "UTF-8", 0));
		});
	}

	@Test
	public void openOutputStream_createsParentsAndSupportsAppend() throws IOException {
		File file = new File(tempDir, "o/p/out.txt");
		try (FileOutputStream out = FileUtils.openOutputStream(file.getAbsolutePath())) {
			out.write('a');
		}
		try (FileOutputStream out = FileUtils.openOutputStream(file.getAbsolutePath(), true)) {
			out.write('b');
		}
		assertEquals("ab", read(file, UTF8));
		try (FileOutputStream out = FileUtils.openOutputStream(file)) {
			out.write('c');
		}
		assertEquals("c", read(file, UTF8));
		assertThrows(IOException.class, () -> FileUtils.openOutputStream(tempDir));
		assertThrows(IllegalArgumentException.class, () -> FileUtils.openOutputStream((File) null));
	}

	@Test
	public void openOutputStream_failsForReadOnlyFile() throws IOException {
		File file = write("ro.txt", "x", UTF8);
		assumeTrue(file.setWritable(false));
		try {
			assumeFalse(file.canWrite(), "running with privileges that ignore permissions");
			assertThrows(IOException.class, () -> FileUtils.openOutputStream(file));
		} finally {
			file.setWritable(true);
		}
	}

	@Test
	public void writers_encodeWithCharset() throws IOException {
		File file = new File(tempDir, "w.txt");
		try (BufferedWriter writer = FileUtils.getBufferedWriter(file.getAbsolutePath(), GBK)) {
			writer.write(CHINESE);
		}
		assertEquals(CHINESE, read(file, GBK));
		try (BufferedWriter writer = FileUtils.getBufferedWriter(file, true, GBK)) {
			writer.write("!");
		}
		assertEquals(CHINESE + "!", read(file, GBK));
		try (BufferedWriter writer = FileUtils.getBufferedWriter(file, UTF8)) {
			writer.write("x");
		}
		assertEquals("x", read(file, UTF8));
	}

	// ---------------------------------------------------------------- formatSize

	@Test
	public void formatSize_choosesLargestUnit() {
		assertEquals("0.00 bytes", FileUtils.formatSize(0));
		assertEquals("1000.00 bytes", FileUtils.formatSize(1000));
		// values that round up to one unit are displayed in that unit
		assertEquals("1.00 KB", FileUtils.formatSize(1023));
		assertEquals("1.00 KB", FileUtils.formatSize(1024));
		assertEquals("1.5 KB", FileUtils.formatSize(1536, 1));
		assertEquals("1.00 MB", FileUtils.formatSize(FileUtils.MB));
		assertEquals("2.00 GB", FileUtils.formatSize(2 * FileUtils.GB));
		assertEquals("1.00 TB", FileUtils.formatSize(FileUtils.TB));
		assertEquals("1.00 PB", FileUtils.formatSize(FileUtils.PB));
		assertEquals("1.00 EB", FileUtils.formatSize(FileUtils.EB));
		assertEquals("1.00 ZB", FileUtils.formatSize(FileUtils.ZB));
		assertEquals("3.00 YB", FileUtils.formatSize(FileUtils.YB.multiply(BigInteger.valueOf(3))));
		assertEquals("8.00 EB", FileUtils.formatSize(Long.MAX_VALUE));
		assertThrows(IllegalArgumentException.class, () -> FileUtils.formatSize((BigInteger) null));
	}

	// ---------------------------------------------------------------- newer / older

	@Test
	public void isFileNewerAndOlder_compareLastModified() throws IOException {
		File old = write("old.txt", "", UTF8);
		File young = write("young.txt", "", UTF8);
		assertTrue(old.setLastModified(10_000L));
		assertTrue(young.setLastModified(20_000L));
		assertTrue(FileUtils.isFileNewer(young, old));
		assertFalse(FileUtils.isFileNewer(old, young));
		assertTrue(FileUtils.isFileNewer(young, new Date(15_000L)));
		assertTrue(FileUtils.isFileOlder(old, young));
		assertFalse(FileUtils.isFileOlder(young, old));
		assertTrue(FileUtils.isFileOlder(old, new Date(15_000L)));
		assertThrows(FileNotFoundException.class, () -> FileUtils.isFileNewer(young, new File(tempDir, "none")));
		assertThrows(FileNotFoundException.class, () -> FileUtils.isFileOlder(new File(tempDir, "none"), 0L));
		assertThrows(IllegalArgumentException.class, () -> FileUtils.isFileNewer(young, (Date) null));
		assertThrows(IllegalArgumentException.class, () -> FileUtils.isFileOlder(young, (Date) null));
	}

	// ---------------------------------------------------------------- symlink and delete

	@Test
	public void isSymlink_detectsSymbolicLinks() throws IOException {
		File target = write("target.txt", "t", UTF8);
		File link = new File(tempDir, "link.txt");
		try {
			Files.createSymbolicLink(link.toPath(), target.toPath());
		} catch (UnsupportedOperationException | IOException e) {
			assumeTrue(false, "symbolic links not supported");
		}
		assertTrue(FileUtils.isSymlink(link));
		assertFalse(FileUtils.isSymlink(target));
		assertFalse(FileUtils.isSymlink(new File("relative-name")));
		assertThrows(IllegalArgumentException.class, () -> FileUtils.isSymlink(null));
	}

	@Test
	public void deleteDirectory_removesTreeButNotSymlinkTargets() throws IOException {
		File root = createTree("del");
		File outside = createTree("outside");
		try {
			Files.createSymbolicLink(new File(root, "link").toPath(), outside.toPath());
		} catch (UnsupportedOperationException | IOException e) {
			// symlinks unsupported: the rest of the test still applies
		}
		FileUtils.deleteDirectory(root);
		assertFalse(root.exists());
		assertTrue(new File(outside, "sub/deep/c.txt").exists());
	}

	@Test
	public void cleanDirectory_keepsDirectoryItself() throws IOException {
		File root = createTree("clean");
		FileUtils.cleanDirectory(root);
		assertTrue(root.isDirectory());
		assertEquals(0, root.list().length);
		File file = write("plain.txt", "", UTF8);
		assertThrows(IOException.class, () -> FileUtils.cleanDirectory(file));
		assertThrows(IllegalArgumentException.class, () -> FileUtils.cleanDirectory(null));
	}

	@Test
	public void deleteFile_deletesFilesAndDirectories() throws IOException {
		File file = write("d.txt", "", UTF8);
		FileUtils.deleteFile(file);
		assertFalse(file.exists());
		File dir = createTree("deldir");
		FileUtils.deleteFile(dir);
		assertFalse(dir.exists());
		assertThrows(FileNotFoundException.class, () -> FileUtils.deleteFile(new File(tempDir, "missing")));
		assertThrows(IllegalArgumentException.class, () -> FileUtils.deleteFile(null));
	}

	@Test
	public void deleteFileOnExit_acceptsFilesAndDirectories() throws IOException {
		File file = write("exit/e.txt", "", UTF8);
		FileUtils.deleteFileOnExit(file);
		FileUtils.deleteFileOnExit(createTree("exitdir"));
		assertTrue(file.exists());
		assertThrows(FileNotFoundException.class, () -> FileUtils.deleteFileOnExit(new File(tempDir, "missing")));
		assertThrows(IllegalArgumentException.class, () -> FileUtils.deleteFileOnExit(null));
	}

	// ---------------------------------------------------------------- reading

	@Test
	public void readFileToString_returnsContentUnchanged() throws IOException {
		File file = write("r.txt", "a\r\nb", UTF8);
		assertEquals("a\r\nb", FileUtils.readFileToString(file, UTF8));
		assertEquals("a\r\nb", FileUtils.readFileToString(file.getAbsolutePath(), UTF8));
		File mixed = write("m.txt", "line1\nline2\n中文", UTF8);
		assertEquals("line1\nline2\n中文", FileUtils.readFileToString(mixed, UTF8));
		File trailing = write("t.txt", "x\n", UTF8);
		assertEquals("x\n", FileUtils.readFileToString(trailing, UTF8));
		File gbk = write("g.txt", CHINESE, GBK);
		assertEquals(CHINESE, FileUtils.readFileToString(gbk, GBK));
		assertEquals("", FileUtils.readFileToString(write("empty.txt", "", UTF8), UTF8));
		assertThrows(FileNotFoundException.class, () -> FileUtils.readFileToString(new File(tempDir, "none"), UTF8));
		assertThrows(IllegalArgumentException.class, () -> FileUtils.readFileToString((File) null, UTF8));
	}

	@Test
	public void existsAndAvailability_areNullSafe() throws IOException {
		File file = write("e.txt", "", UTF8);
		assertTrue(FileUtils.exists(file));
		assertTrue(FileUtils.exists(file.getAbsolutePath()));
		assertFalse(FileUtils.exists((File) null));
		assertFalse(FileUtils.exists((String) null));
		assertTrue(FileUtils.notExists(new File(tempDir, "x")));
		assertTrue(FileUtils.notExists(new File(tempDir, "x").getAbsolutePath()));
		assertTrue(FileUtils.isAvailable(file));
		assertTrue(FileUtils.isAvailable(file.getAbsolutePath()));
		assertFalse(FileUtils.isAvailable((String) null));
		assertFalse(FileUtils.isAvailable((File) null));
		assertTrue(FileUtils.isNotAvailable(new File(tempDir, "x")));
		assertTrue(FileUtils.isNotAvailable(new File(tempDir, "x").getAbsolutePath()));
	}

	@Test
	public void readLines_decodesUtf8ByDefaultAndGbkOnRequest() throws IOException {
		assertEquals(Arrays.asList(CHINESE, "b"), FileUtils.readLines(write("u.txt", CHINESE + "\nb", UTF8)));
		assertEquals(Arrays.asList(CHINESE, ""), FileUtils.readLines(write("g.txt", CHINESE + "\n\n", GBK), GBK));
		assertEquals(Collections.emptyList(), FileUtils.readLines(write("e.txt", "", UTF8)));
		assertThrows(IllegalArgumentException.class, () -> FileUtils.readLines(null, UTF8));
	}

	@Test
	public void getLineNumber_countsLineTerminators() throws IOException {
		assertEquals(3, FileUtils.getLineNumber(write("l.txt", "1\n2\n3\n", UTF8), UTF8));
		assertEquals(0, FileUtils.getLineNumber(write("e.txt", "", UTF8), UTF8));
		assertEquals(2, FileUtils.getLineNumber(write("g.txt", "中\n文\n", GBK), GBK));
	}

	@Test
	public void readFileToByteAndCharArray() throws IOException {
		byte[] data = new byte[100_000];
		new Random(1).nextBytes(data);
		File file = new File(tempDir, "bin");
		Files.write(file.toPath(), data);
		assertArrayEquals(data, FileUtils.readFileToByteArray(file));
		assertArrayEquals(CHINESE.toCharArray(), FileUtils.readFileToCharArray(write("c.txt", CHINESE, GBK), GBK));
		assertThrows(IllegalArgumentException.class, () -> FileUtils.readFileToCharArray(null, GBK));
		assertThrows(FileNotFoundException.class, () -> FileUtils.readFileToByteArray(new File(tempDir, "none")));
	}

	// ---------------------------------------------------------------- split and merge

	@Test
	public void split_writesChunksIncludingAnUnterminatedLastLine() throws IOException {
		File file = write("data.txt", "1\n2\n3\n4\n5", UTF8);
		File output = new File(tempDir, "parts");
		FileUtils.split(file, UTF8, 2, output, "part-#0");
		assertEquals("1" + NL + "2", read(new File(output, "part-1.txt"), UTF8));
		assertEquals("3" + NL + "4", read(new File(output, "part-2.txt"), UTF8));
		assertEquals("5" + NL, read(new File(output, "part-3.txt"), UTF8));
		assertEquals(3, output.list().length);
	}

	@Test
	public void split_withDefaultCharsetAndInvalidArguments() throws IOException {
		File file = write("d.txt", "a\nb\n", Charset.defaultCharset());
		File output = new File(tempDir, "out");
		FileUtils.split(file, 1, output, "p#0");
		assertEquals("a", read(new File(output, "p1.txt"), Charset.defaultCharset()));
		assertEquals("b", read(new File(output, "p2.txt"), Charset.defaultCharset()));
		assertThrows(IllegalArgumentException.class, () -> FileUtils.split(file, UTF8, 0, output, "p#0"));
		assertThrows(IllegalArgumentException.class, () -> FileUtils.split(file, UTF8, 1, null, "p#0"));
		assertThrows(IllegalArgumentException.class, () -> FileUtils.split(file, UTF8, 1, output, " "));
	}

	@Test
	public void mergeTo_concatenatesFiles() throws IOException {
		File a = write("a.txt", "A", UTF8);
		File b = write("b.txt", CHINESE, UTF8);
		File out = new File(tempDir, "merged.txt");
		FileUtils.mergeTo(new File[] { a, b }, out, UTF8);
		assertEquals("A" + NL + CHINESE, read(out, UTF8));
		FileUtils.mergeTo(null, out, UTF8);
		assertEquals("", read(out, UTF8));
	}

	// ---------------------------------------------------------------- writing

	@Test
	public void writeFile_bytesStreamReaderCharsAndText() throws IOException {
		File file = new File(tempDir, "w/out.txt");
		FileUtils.writeFile("ab".getBytes(UTF8), file, false);
		FileUtils.writeFile(new ByteArrayInputStream("cd".getBytes(UTF8)), file, true);
		FileUtils.writeFile(new StringReader("中"), file, true, UTF8);
		FileUtils.writeFile("ef".toCharArray(), file, true, UTF8);
		FileUtils.writeFile(new StringBuilder("gh"), file, true, UTF8);
		assertEquals("abcd中efgh", read(file, UTF8));
		FileUtils.writeFile(Arrays.asList("x", "y"), file, false, GBK);
		assertEquals("x" + NL + "y" + NL, read(file, GBK));
		FileUtils.clearFile(file, UTF8);
		assertEquals(0, file.length());
	}

	@Test
	public void writeFile_rejectsNullArguments() {
		File file = new File(tempDir, "n.txt");
		assertThrows(IllegalArgumentException.class, () -> FileUtils.writeFile((byte[]) null, file, false));
		assertThrows(IllegalArgumentException.class, () -> FileUtils.writeFile(new byte[0], null, false));
		assertThrows(IllegalArgumentException.class, () -> FileUtils.writeFile((java.io.InputStream) null, file, false));
		assertThrows(IllegalArgumentException.class, () -> FileUtils.writeFile(new ByteArrayInputStream(new byte[0]), null, false));
		assertThrows(IllegalArgumentException.class, () -> FileUtils.writeFile((java.io.Reader) null, file, false, UTF8));
		assertThrows(IllegalArgumentException.class, () -> FileUtils.writeFile(new StringReader(""), null, false, UTF8));
		assertThrows(IllegalArgumentException.class, () -> FileUtils.writeFile((char[]) null, file, false, UTF8));
		assertThrows(IllegalArgumentException.class, () -> FileUtils.writeFile(new char[0], null, false, UTF8));
		assertThrows(IllegalArgumentException.class, () -> FileUtils.writeFile((CharSequence) null, file, false, UTF8));
		assertThrows(IllegalArgumentException.class, () -> FileUtils.writeFile("", null, false, UTF8));
		assertThrows(IllegalArgumentException.class, () -> FileUtils.writeFile((java.util.Collection<String>) null, file, false, UTF8));
		assertThrows(IllegalArgumentException.class, () -> FileUtils.writeFile(new ArrayList<String>(), null, false, UTF8));
	}

	@Test
	public void writeFile_largeContentIsWrittenCompletely() throws IOException {
		byte[] data = new byte[4 * 1024 * 1024 + 3];
		new Random(7).nextBytes(data);
		File file = new File(tempDir, "large.bin");
		FileUtils.writeFile(data, file, false);
		assertArrayEquals(data, Files.readAllBytes(file.toPath()));
		File copy = new File(tempDir, "large-copy.bin");
		FileUtils.copyFile(file, copy);
		assertArrayEquals(data, Files.readAllBytes(copy.toPath()));
	}

	// ---------------------------------------------------------------- move

	@Test
	public void moveFile_movesContent() throws IOException {
		File src = write("m/src.txt", CHINESE, UTF8);
		File dest = new File(tempDir, "m2/dest.txt");
		FileUtils.moveFile(src, dest);
		assertFalse(src.exists());
		assertEquals(CHINESE, read(dest, UTF8));
		assertThrows(FileNotFoundException.class, () -> FileUtils.moveFile(src, dest));
	}

	@Test
	public void moveFileToDirectory_andMoveToDirectory() throws IOException {
		File src = write("mv/a.txt", "a", UTF8);
		File destDir = new File(tempDir, "target");
		FileUtils.moveFileToDirectory(src, destDir);
		assertEquals("a", read(new File(destDir, "a.txt"), UTF8));

		File other = write("mv/b.txt", "b", UTF8);
		FileUtils.moveToDirectory(other, destDir, true);
		assertTrue(new File(destDir, "b.txt").exists());

		File tree = createTree("tree");
		FileUtils.moveToDirectory(tree, destDir, true);
		assertFalse(tree.exists());
		assertEquals(CHINESE, read(new File(destDir, "tree/sub/deep/c.txt"), UTF8));

		assertThrows(IllegalArgumentException.class, () -> FileUtils.moveFileToDirectory(null, destDir));
		assertThrows(IllegalArgumentException.class, () -> FileUtils.moveFileToDirectory(other, null));
		assertThrows(IllegalArgumentException.class, () -> FileUtils.moveToDirectory(null, destDir, true));
		assertThrows(IllegalArgumentException.class, () -> FileUtils.moveToDirectory(other, null, true));
		assertThrows(IllegalArgumentException.class, () -> FileUtils.moveDirectoryToDirectory(null, destDir));
		assertThrows(IllegalArgumentException.class, () -> FileUtils.moveDirectoryToDirectory(tree, null));
	}

	@Test
	public void moveDirectory_keepsWholeTree() throws IOException {
		File src = createTree("srcdir");
		Set<String> expected = relativePaths(src);
		File dest = new File(tempDir, "destdir");
		FileUtils.moveDirectory(src, dest);
		assertFalse(src.exists());
		assertEquals(expected, relativePaths(dest));
	}

	@Test
	public void moveDirectory_rejectsOwnSubdirectoryButAcceptsSiblingWithCommonPrefix() throws IOException {
		File src = createTree("foo");
		File sub = new File(src, "sub/inner");
		IOException e = assertThrows(IOException.class, () -> FileUtils.moveDirectory(src, sub));
		assertTrue(e.getMessage().contains("subdirectory of itself"));
		assertFalse(sub.exists());
		File sibling = new File(tempDir, "foobar");
		FileUtils.moveDirectory(src, sibling);
		assertTrue(new File(sibling, "sub/deep/c.txt").exists());
		assertThrows(IllegalArgumentException.class, () -> FileUtils.moveDirectory(sibling, null));
		assertThrows(FileNotFoundException.class, () -> FileUtils.moveDirectory(new File(tempDir, "none"), sub));
	}

	// ---------------------------------------------------------------- copy

	@Test
	public void copyFile_copiesContentAndRejectsSameFile() throws IOException {
		File src = write("c/src.txt", CHINESE, GBK);
		File dest = new File(tempDir, "c2/dest.txt");
		FileUtils.copyFile(src, dest);
		assertEquals(CHINESE, read(dest, GBK));
		assertTrue(src.exists());
		IOException e = assertThrows(IOException.class, () -> FileUtils.copyFile(src, src));
		assertTrue(e.getMessage().contains("are the same"));
		assertThrows(IllegalArgumentException.class, () -> FileUtils.copyFile(src, (File) null));
		assertThrows(FileNotFoundException.class, () -> FileUtils.copyFile(new File(tempDir, "none"), dest));
	}

	@Test
	public void copyFile_emptyFile() throws IOException {
		File src = write("empty.txt", "", UTF8);
		File dest = new File(tempDir, "empty-copy.txt");
		FileUtils.copyFile(src, dest);
		assertTrue(dest.exists());
		assertEquals(0, dest.length());
	}

	@Test
	public void copyFileToDirectory_andCopyFilesToDirectory() throws IOException {
		File a = write("a.txt", "a", UTF8);
		File b = write("b.txt", "b", UTF8);
		File destDir = new File(tempDir, "copies");
		FileUtils.copyFileToDirectory(a, destDir);
		assertEquals("a", read(new File(destDir, "a.txt"), UTF8));
		FileUtils.copyFilesToDirectory(new File[] { a, b }, new File(tempDir, "copies2"));
		assertEquals("b", read(new File(tempDir, "copies2/b.txt"), UTF8));
		assertThrows(FileNotFoundException.class, () -> FileUtils.copyFileToDirectory(new File(tempDir, "none"), destDir));
		assertThrows(IllegalArgumentException.class, () -> FileUtils.copyFileToDirectory(null, destDir));
	}

	@Test
	public void copyDirectory_copiesNestedDirectoriesCompletely() throws IOException {
		File src = createTree("src");
		File dest = new File(tempDir, "dest");
		FileUtils.copyDirectory(src, dest);
		assertEquals(relativePaths(src), relativePaths(dest));
		assertEquals(CHINESE, read(new File(dest, "sub/deep/c.txt"), UTF8));
		assertTrue(new File(dest, "empty").isDirectory());
	}

	@Test
	public void copyDirectory_appliesFilter() throws IOException {
		File src = createTree("fsrc");
		File dest = new File(tempDir, "fdest");
		FileUtils.copyDirectory(src, dest, file -> file.isDirectory() || file.getName().startsWith("b"));
		assertTrue(new File(dest, "sub/b.txt").exists());
		assertFalse(new File(dest, "a.txt").exists());
		assertFalse(new File(dest, "sub/deep/c.txt").exists());
	}

	@Test
	public void copyDirectory_intoOwnSubdirectoryDoesNotRecurseForever() throws IOException {
		File src = createTree("self");
		File dest = new File(src, "backup");
		FileUtils.copyDirectory(src, dest);
		assertTrue(new File(dest, "sub/deep/c.txt").exists());
		assertFalse(new File(dest, "backup").exists());
	}

	@Test
	public void copyDirectory_rejectsSameDirectoryAndMissingSource() throws IOException {
		File src = createTree("same");
		assertThrows(IOException.class, () -> FileUtils.copyDirectory(src, src));
		assertThrows(FileNotFoundException.class, () -> FileUtils.copyDirectory(new File(tempDir, "none"), src));
		assertThrows(IOException.class, () -> FileUtils.copyDirectory(new File(src, "a.txt"), new File(tempDir, "x")));
		assertThrows(IllegalArgumentException.class, () -> FileUtils.copyDirectory(src, null));
	}

	@Test
	public void copyFile_toStreamsWritersAndCollections() throws IOException {
		File file = write("s.txt", CHINESE + "\nB", UTF8);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		assertEquals(file.length(), FileUtils.copyFile(file, out));
		assertEquals(CHINESE + "\nB", new String(out.toByteArray(), UTF8));

		StringWriter writer = new StringWriter();
		FileUtils.copyFile(file, writer, "UTF-8");
		assertEquals(CHINESE + "\nB", writer.toString());

		File gbk = write("gbk.txt", CHINESE, GBK);
		List<String> lines = new ArrayList<String>();
		FileUtils.copyFile(gbk, lines, GBK);
		assertEquals(Collections.singletonList(CHINESE), lines);

		StringBuilder builder = new StringBuilder();
		assertEquals(3, FileUtils.copyFile(file, builder, UTF8));
		StringBuffer buffer = new StringBuffer();
		assertEquals(3, FileUtils.copyFile(file, buffer, UTF8));
		assertEquals(builder.toString(), buffer.toString());

		File ascii = write("ascii.txt", "q\nr", UTF8);
		writer = new StringWriter();
		FileUtils.copyFile(ascii, writer);
		assertEquals("q\nr", writer.toString());
		lines.clear();
		FileUtils.copyFile(ascii, lines);
		assertEquals(Arrays.asList("q", "r"), lines);
	}

	// ---------------------------------------------------------------- listing and scanning

	@Test
	public void listFiles_returnsDirectoriesBeforeFiles() throws IOException {
		File root = createTree("list");
		List<File> files = FileUtils.listFiles(root, null);
		assertEquals(3, files.size());
		assertTrue(files.get(0).isDirectory());
		assertTrue(files.get(1).isDirectory());
		assertEquals("a.txt", files.get(2).getName());
		List<String> paths = FileUtils.list(root, file -> file.isFile());
		assertEquals(Collections.singletonList(new File(root, "a.txt").getAbsolutePath()), paths);
		assertTrue(FileUtils.listFiles(new File(root, "empty"), null).isEmpty());
		assertThrows(IOException.class, () -> FileUtils.listFiles(new File(root, "a.txt"), null));
		assertThrows(IOException.class, () -> FileUtils.listFiles(new File(tempDir, "none"), null));
	}

	@Test
	public void sizeOfDirectory_sumsAllFilesRecursively() throws IOException {
		File root = createTree("size");
		long expected = 1 + 2 + CHINESE.getBytes(UTF8).length;
		assertEquals(expected, FileUtils.sizeOfDirectory(root, null));
		assertEquals(1, FileUtils.sizeOfDirectory(root, file -> file.getName().equals("a.txt")));
	}

	@Test
	public void scan_visitsEveryFileWithDepthAndHonorsDirectoryFilter() throws IOException {
		File root = createTree("scan");
		final List<String> visited = new ArrayList<String>();
		FileUtils.scan(root, null, (dir, depth, file) -> visited.add(depth + ":" + file.getName()));
		Collections.sort(visited);
		assertEquals(Arrays.asList("0:a.txt", "1:b.txt", "2:c.txt"), visited);

		visited.clear();
		FileUtils.scan(root, null, new ScanFilter() {
			@Override
			public boolean filterDirectory(File directory, int depth) {
				return !directory.getName().equals("deep");
			}

			@Override
			public void filterFile(File directory, int depth, File file) {
				visited.add(file.getName());
			}
		});
		Collections.sort(visited);
		assertEquals(Arrays.asList("a.txt", "b.txt"), visited);
		assertThrows(IOException.class, () -> FileUtils.scan(new File(tempDir, "none"), null, (d, depth, f) -> {
		}));
	}

	@Test
	public void scan_skipsSymbolicLinks() throws IOException {
		File root = createTree("scanlink");
		File outside = write("outside.txt", "o", UTF8);
		try {
			Files.createSymbolicLink(new File(root, "link.txt").toPath(), outside.toPath());
		} catch (UnsupportedOperationException | IOException e) {
			assumeTrue(false, "symbolic links not supported");
		}
		final List<String> visited = new ArrayList<String>();
		FileUtils.scan(root, null, (dir, depth, file) -> visited.add(file.getName()));
		assertFalse(visited.contains("link.txt"));
		assertEquals(3, visited.size());
	}

	// ---------------------------------------------------------------- insertContent

	@Test
	public void insertContent_insertsAtPosition() throws IOException {
		File file = write("ins.txt", "Hello World", UTF8);
		FileUtils.insertContent(file, 5, ",中", UTF8);
		assertEquals("Hello,中 World", read(file, UTF8));
		FileUtils.insertContent(file, 0, ">", UTF8);
		assertEquals(">Hello,中 World", read(file, UTF8));
		FileUtils.insertContent(file, (int) file.length(), "<", UTF8);
		assertEquals(">Hello,中 World<", read(file, UTF8));
		assertThrows(FileNotFoundException.class, () -> FileUtils.insertContent(new File(tempDir, "none"), 0, "x", UTF8));
		assertThrows(IllegalArgumentException.class, () -> FileUtils.insertContent(file, 0, null, UTF8));
	}

	// ---------------------------------------------------------------- resource leaks

	@Test
	public void fileOperations_doNotLeakFileHandles() throws Exception {
		final File file = write("leak.txt", "a\nb\nc\n", UTF8);
		final File dest = new File(tempDir, "leak-dest.txt");
		final File dir = createTree("leakdir");
		assertNoFileHandleLeak(() -> {
			FileUtils.readFileToString(file, UTF8);
			FileUtils.readLines(file);
			FileUtils.readFileToByteArray(file);
			FileUtils.readFileToCharArray(file, UTF8);
			FileUtils.getLineNumber(file, UTF8);
			FileUtils.writeFile("x", dest, false, UTF8);
			FileUtils.writeFile("x".getBytes(UTF8), dest, true);
			FileUtils.copyFile(file, dest);
			FileUtils.copyFile(file, new ByteArrayOutputStream());
			FileUtils.copyFile(file, new StringWriter(), "UTF-8");
			FileUtils.insertContent(dest, 0, "y", UTF8);
			FileUtils.sizeOfDirectory(dir, null);
		});
	}

	@Test
	public void failingReads_doNotLeakFileHandles() throws Exception {
		final File missing = new File(tempDir, "missing.txt");
		assertNoFileHandleLeak(() -> {
			try {
				FileUtils.readFileToString(missing, UTF8);
			} catch (FileNotFoundException expected) {
				assertNotNull(expected.getMessage());
			}
		});
	}
}
