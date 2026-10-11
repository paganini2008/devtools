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
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * ZipUtilsTest
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class ZipUtilsTest {

	private static final Charset GBK = Charset.forName("GBK");

	@TempDir
	Path tmp;

	// ----------------------------------------------------------------- helpers

	private static File write(File file, byte[] content) throws IOException {
		file.getParentFile().mkdirs();
		Files.write(file.toPath(), content);
		return file;
	}

	private static File write(File file, String content) throws IOException {
		return write(file, content.getBytes(StandardCharsets.UTF_8));
	}

	private static byte[] randomBytes(int size, long seed) {
		byte[] data = new byte[size];
		new Random(seed).nextBytes(data);
		return data;
	}

	/**
	 * Builds a sample tree: docs/a.txt, docs/sub/b.bin, docs/empty/, docs/中文目录/文件.txt
	 */
	private File sampleTree() throws IOException {
		File docs = tmp.resolve("src").resolve("docs").toFile();
		write(new File(docs, "a.txt"), "hello");
		write(new File(docs, "sub/b.bin"), randomBytes(10000, 1L));
		new File(docs, "empty").mkdirs();
		write(new File(docs, "中文目录/文件.txt"), "中文内容");
		return docs;
	}

	/** Relative path (with '/') to content (null for directories). */
	private static TreeMap<String, byte[]> snapshot(File root) throws IOException {
		TreeMap<String, byte[]> map = new TreeMap<String, byte[]>();
		snapshot(root, "", map);
		return map;
	}

	private static void snapshot(File dir, String prefix, TreeMap<String, byte[]> map) throws IOException {
		for (File f : dir.listFiles()) {
			String name = prefix + f.getName();
			if (f.isDirectory()) {
				map.put(name + "/", null);
				snapshot(f, name + "/", map);
			} else {
				map.put(name, Files.readAllBytes(f.toPath()));
			}
		}
	}

	private static void assertSameTree(File expected, File actual) throws IOException {
		TreeMap<String, byte[]> e = snapshot(expected);
		TreeMap<String, byte[]> a = snapshot(actual);
		assertEquals(e.keySet(), a.keySet());
		for (String key : e.keySet()) {
			assertArrayEquals(e.get(key), a.get(key), key);
		}
	}

	private static byte[] zipBytes(Charset charset, String... namesAndContents) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try (ZipOutputStream zos = new ZipOutputStream(bos, charset)) {
			for (int i = 0; i < namesAndContents.length; i += 2) {
				zos.putNextEntry(new ZipEntry(namesAndContents[i]));
				if (namesAndContents[i + 1] != null) {
					zos.write(namesAndContents[i + 1].getBytes(StandardCharsets.UTF_8));
				}
				zos.closeEntry();
			}
		}
		return bos.toByteArray();
	}

	private static final class TrackingOutputStream extends ByteArrayOutputStream {
		boolean closed;

		@Override
		public void close() throws IOException {
			closed = true;
			super.close();
		}
	}

	private static final class TrackingInputStream extends ByteArrayInputStream {
		boolean closed;

		TrackingInputStream(byte[] buf) {
			super(buf);
		}

		@Override
		public void close() throws IOException {
			closed = true;
			super.close();
		}
	}

	// --------------------------------------------------------------------- zip

	@Test
	public void zipDirectory_roundTripKeepsContentEmptyDirsAndChineseNames() throws IOException {
		File docs = sampleTree();
		File zip = tmp.resolve("out/docs.zip").toFile();
		ZipUtils.zip(docs, zip);
		assertTrue(zip.isFile());

		assertEquals(Arrays.asList("docs/", "docs/a.txt", "docs/empty/", "docs/sub/", "docs/sub/b.bin",
				"docs/中文目录/", "docs/中文目录/文件.txt"), ZipUtils.listEntries(zip));

		File dest = tmp.resolve("dest").toFile();
		ZipUtils.unzip(zip, dest);
		assertSameTree(docs, new File(dest, "docs"));
		assertTrue(new File(dest, "docs/empty").isDirectory());
		assertEquals("中文内容",
				new String(Files.readAllBytes(new File(dest, "docs/中文目录/文件.txt").toPath()), StandardCharsets.UTF_8));
	}

	@Test
	public void zipSingleFile_storesFileUnderItsName() throws IOException {
		File file = write(tmp.resolve("one.txt").toFile(), "single");
		File zip = tmp.resolve("one.zip").toFile();
		ZipUtils.zip(file, zip);
		assertEquals(Collections.singletonList("one.txt"), ZipUtils.listEntries(zip));
		File dest = tmp.resolve("d").toFile();
		ZipUtils.unzip(zip, dest);
		assertEquals("single", new String(Files.readAllBytes(new File(dest, "one.txt").toPath()), StandardCharsets.UTF_8));
	}

	@Test
	public void zipEmptyFile_andEmptyDirectory() throws IOException {
		File empty = write(tmp.resolve("e/empty.txt").toFile(), new byte[0]);
		File emptyDir = tmp.resolve("e/nothing").toFile();
		emptyDir.mkdirs();
		File zip = tmp.resolve("e.zip").toFile();
		ZipUtils.zip(new File[] { empty, emptyDir }, zip);
		assertEquals(Arrays.asList("empty.txt", "nothing/"), ZipUtils.listEntries(zip));
		File dest = tmp.resolve("d").toFile();
		ZipUtils.unzip(zip, dest);
		assertEquals(0, new File(dest, "empty.txt").length());
		assertTrue(new File(dest, "nothing").isDirectory());
	}

	@Test
	public void zipArray_putsEachSourceAtTopLevel() throws IOException {
		File a = write(tmp.resolve("x/a.txt").toFile(), "A");
		File dir = tmp.resolve("y/dir").toFile();
		write(new File(dir, "b.txt"), "B");
		File zip = tmp.resolve("multi.zip").toFile();
		ZipUtils.zip(new File[] { a, dir }, zip);
		assertEquals(Arrays.asList("a.txt", "dir/", "dir/b.txt"), ZipUtils.listEntries(zip));
	}

	@Test
	public void zipArray_duplicateNamesFail() throws IOException {
		File a1 = write(tmp.resolve("x/a.txt").toFile(), "1");
		File a2 = write(tmp.resolve("y/a.txt").toFile(), "2");
		assertThrows(ZipException.class, () -> ZipUtils.zip(new File[] { a1, a2 }, tmp.resolve("dup.zip").toFile()));
	}

	@Test
	public void zipWithFilter_skipsRejectedFilesAndDirectories() throws IOException {
		File docs = sampleTree();
		write(new File(docs, "skip.log"), "log");
		File zip = tmp.resolve("filtered.zip").toFile();
		ZipUtils.zip(docs, zip, f -> !f.getName().endsWith(".log") && !f.getName().equals("sub"));
		List<String> entries = ZipUtils.listEntries(zip);
		assertTrue(entries.contains("docs/a.txt"));
		assertFalse(entries.contains("docs/skip.log"));
		assertFalse(entries.contains("docs/sub/"));
		assertFalse(entries.contains("docs/sub/b.bin"));

		File zip2 = tmp.resolve("filtered2.zip").toFile();
		ZipUtils.zip(new File[] { docs }, zip2, f -> f.isDirectory());
		assertEquals(Arrays.asList("docs/", "docs/empty/", "docs/sub/", "docs/中文目录/"), ZipUtils.listEntries(zip2));
	}

	@Test
	public void zipToStream_doesNotCloseCallerStream() throws IOException {
		File docs = sampleTree();
		TrackingOutputStream out = new TrackingOutputStream();
		ZipUtils.zip(docs, out);
		assertFalse(out.closed);
		out.write(new byte[0]); // still usable

		File dest = tmp.resolve("d").toFile();
		ZipUtils.unzip(new ByteArrayInputStream(out.toByteArray()), dest);
		assertSameTree(docs, new File(dest, "docs"));

		TrackingOutputStream out2 = new TrackingOutputStream();
		ZipUtils.zip(docs, out2, f -> !f.getName().equals("a.txt"));
		assertFalse(out2.closed);
		File dest2 = tmp.resolve("d2").toFile();
		ZipUtils.unzip(new ByteArrayInputStream(out2.toByteArray()), dest2);
		assertFalse(new File(dest2, "docs/a.txt").exists());
		assertTrue(new File(dest2, "docs/sub/b.bin").exists());
	}

	@Test
	public void zipIntoSourceDirectory_skipsTheZipItself() throws IOException {
		File docs = sampleTree();
		File zip = new File(docs, "self.zip");
		ZipUtils.zip(docs, zip);
		List<String> entries = ZipUtils.listEntries(zip);
		assertFalse(entries.contains("docs/self.zip"));
		assertTrue(entries.contains("docs/a.txt"));
	}

	@Test
	public void zip_doesNotFollowSymlinkedDirectories() throws IOException {
		File docs = sampleTree();
		File outside = tmp.resolve("outside").toFile();
		write(new File(outside, "secret.txt"), "secret");
		try {
			Files.createSymbolicLink(new File(docs, "link").toPath(), outside.toPath());
		} catch (IOException | UnsupportedOperationException e) {
			assumeTrue(false, "symbolic links not supported");
		}
		File zip = tmp.resolve("links.zip").toFile();
		ZipUtils.zip(docs, zip);
		for (String name : ZipUtils.listEntries(zip)) {
			assertFalse(name.startsWith("docs/link"), name);
		}
	}

	@Test
	public void zip_unreadableDirectoryFails() throws IOException {
		File docs = sampleTree();
		File sub = new File(docs, "sub");
		assumeTrue(sub.setReadable(false) && sub.listFiles() == null, "cannot make directory unreadable");
		try {
			IOException e = assertThrows(IOException.class, () -> ZipUtils.zip(docs, tmp.resolve("x.zip").toFile()));
			assertTrue(e.getMessage().startsWith("Cannot list directory"));
		} finally {
			sub.setReadable(true);
		}
	}

	@Test
	public void zip_invalidArguments() throws IOException {
		File file = write(tmp.resolve("f.txt").toFile(), "x");
		File zip = tmp.resolve("z.zip").toFile();
		assertThrows(IllegalArgumentException.class, () -> ZipUtils.zip((File) null, zip));
		assertThrows(IllegalArgumentException.class, () -> ZipUtils.zip(file, (File) null));
		assertThrows(IllegalArgumentException.class, () -> ZipUtils.zip((File[]) null, zip));
		assertThrows(IllegalArgumentException.class, () -> ZipUtils.zip(new File[] { null }, zip));
		assertThrows(IllegalArgumentException.class, () -> ZipUtils.zip((File) null, new ByteArrayOutputStream()));
		assertThrows(IllegalArgumentException.class, () -> ZipUtils.zip(file, (OutputStream) null));

		IOException e = assertThrows(IOException.class, () -> ZipUtils.zip(tmp.resolve("missing").toFile(), zip));
		assertTrue(e.getMessage().startsWith("File not found"));
		assertFalse(zip.exists());
		assertThrows(IOException.class, () -> ZipUtils.zip(tmp.resolve("missing").toFile(), new ByteArrayOutputStream()));

		// parent of the destination is a regular file
		assertThrows(IOException.class, () -> ZipUtils.zip(file, new File(file, "z.zip")));
	}

	// ------------------------------------------------------------------- unzip

	@Test
	public void unzip_rejectsZipSlipEntry() throws IOException {
		File zip = write(tmp.resolve("evil.zip").toFile(), zipBytes(StandardCharsets.UTF_8, "../evil.txt", "pwned"));
		File dest = tmp.resolve("work/dest").toFile();
		IOException e = assertThrows(IOException.class, () -> ZipUtils.unzip(zip, dest));
		assertEquals("Zip entry is outside of the target directory: ../evil.txt", e.getMessage());
		assertFalse(tmp.resolve("work/evil.txt").toFile().exists());
		assertEquals(0, dest.list().length);
	}

	@Test
	public void unzip_rejectsNestedTraversalAndKeepsOutsideUntouched() throws IOException {
		byte[] data = zipBytes(StandardCharsets.UTF_8, "ok.txt", "fine", "sub/", null, "sub/../../../evil2.txt", "pwned");
		File dest = tmp.resolve("work/dest").toFile();
		assertThrows(IOException.class, () -> ZipUtils.unzip(new ByteArrayInputStream(data), dest));
		assertTrue(new File(dest, "ok.txt").isFile());
		assertFalse(tmp.resolve("evil2.txt").toFile().exists());
		assertFalse(tmp.resolve("work/evil2.txt").toFile().exists());
	}

	@Test
	public void unzip_rejectsSiblingDirectoryWithSamePrefix() throws IOException {
		// "../dest-evil/x" resolves to a sibling whose path string starts with the dest path
		byte[] data = zipBytes(StandardCharsets.UTF_8, "../dest-evil/x.txt", "pwned");
		File dest = tmp.resolve("dest").toFile();
		assertThrows(IOException.class, () -> ZipUtils.unzip(new ByteArrayInputStream(data), dest));
		assertFalse(tmp.resolve("dest-evil").toFile().exists());
	}

	@Test
	public void unzip_rejectsFileEntryResolvingToDestDirItself() throws IOException {
		byte[] data = zipBytes(StandardCharsets.UTF_8, ".", "x");
		assertThrows(IOException.class, () -> ZipUtils.unzip(new ByteArrayInputStream(data), tmp.resolve("d").toFile()));
		// but a "./" directory entry is harmless
		ZipUtils.unzip(new ByteArrayInputStream(zipBytes(StandardCharsets.UTF_8, "./", null)), tmp.resolve("d2").toFile());
		assertTrue(tmp.resolve("d2").toFile().isDirectory());
	}

	@Test
	public void unzip_rejectsEntryEscapingThroughExistingSymlink() throws IOException {
		File dest = tmp.resolve("dest").toFile();
		dest.mkdirs();
		File outside = tmp.resolve("outside").toFile();
		outside.mkdirs();
		try {
			Files.createSymbolicLink(new File(dest, "link").toPath(), outside.toPath());
		} catch (IOException | UnsupportedOperationException e) {
			assumeTrue(false, "symbolic links not supported");
		}
		byte[] data = zipBytes(StandardCharsets.UTF_8, "link/evil.txt", "pwned");
		assertThrows(IOException.class, () -> ZipUtils.unzip(new ByteArrayInputStream(data), dest));
		assertEquals(0, outside.list().length);
	}

	@Test
	public void unzip_createsParentDirectoriesAndOverwrites() throws IOException {
		File dest = tmp.resolve("dest").toFile();
		write(new File(dest, "a/b/c.txt"), "old");
		byte[] data = zipBytes(StandardCharsets.UTF_8, "a/b/c.txt", "new", "x/y/z.txt", "deep");
		ZipUtils.unzip(new ByteArrayInputStream(data), dest);
		assertEquals("new", new String(Files.readAllBytes(new File(dest, "a/b/c.txt").toPath()), StandardCharsets.UTF_8));
		assertEquals("deep", new String(Files.readAllBytes(new File(dest, "x/y/z.txt").toPath()), StandardCharsets.UTF_8));
	}

	@Test
	public void unzip_gbkEntryNames() throws IOException {
		String name = "报表/中文.txt";
		File zip = write(tmp.resolve("gbk.zip").toFile(), zipBytes(GBK, name, "gbk"));
		assertEquals(Collections.singletonList(name), ZipUtils.listEntries(zip, GBK));

		File dest = tmp.resolve("dest").toFile();
		ZipUtils.unzip(zip, dest, GBK);
		assertEquals("gbk", new String(Files.readAllBytes(new File(dest, name).toPath()), StandardCharsets.UTF_8));

		File dest2 = tmp.resolve("dest2").toFile();
		try (InputStream in = Files.newInputStream(zip.toPath())) {
			ZipUtils.unzip(in, dest2, GBK);
		}
		assertTrue(new File(dest2, name).isFile());
	}

	@Test
	public void unzip_nullCharsetMeansUtf8() throws IOException {
		File docs = sampleTree();
		File zip = tmp.resolve("docs.zip").toFile();
		ZipUtils.zip(docs, zip);
		assertEquals(ZipUtils.listEntries(zip), ZipUtils.listEntries(zip, null));
		File dest = tmp.resolve("d").toFile();
		ZipUtils.unzip(zip, dest, null);
		assertSameTree(docs, new File(dest, "docs"));
	}

	@Test
	public void unzipStream_doesNotCloseCallerStream() throws IOException {
		TrackingInputStream in = new TrackingInputStream(zipBytes(StandardCharsets.UTF_8, "a.txt", "A"));
		ZipUtils.unzip(in, tmp.resolve("d").toFile());
		assertFalse(in.closed);
		assertTrue(tmp.resolve("d/a.txt").toFile().isFile());
	}

	@Test
	public void unzip_emptyStreamCreatesDestOnly() throws IOException {
		File dest = tmp.resolve("empty-dest").toFile();
		ZipUtils.unzip(new ByteArrayInputStream(new byte[0]), dest);
		assertTrue(dest.isDirectory());
		assertEquals(0, dest.list().length);
	}

	@Test
	public void largeFile_roundTrip() throws IOException {
		byte[] big = randomBytes(5 * 1024 * 1024 + 17, 2026L);
		File file = write(tmp.resolve("big/big.bin").toFile(), big);
		File zip = tmp.resolve("big.zip").toFile();
		ZipUtils.zip(file.getParentFile(), zip);
		File dest = tmp.resolve("d").toFile();
		ZipUtils.unzip(zip, dest);
		assertArrayEquals(big, Files.readAllBytes(new File(dest, "big/big.bin").toPath()));
	}

	@Test
	public void unzip_invalidArguments() throws IOException {
		File dest = tmp.resolve("d").toFile();
		assertThrows(IllegalArgumentException.class, () -> ZipUtils.unzip((File) null, dest));
		assertThrows(IllegalArgumentException.class, () -> ZipUtils.unzip((InputStream) null, dest));
		assertThrows(IllegalArgumentException.class, () -> ZipUtils.unzip(new ByteArrayInputStream(new byte[0]), null));
		File zip = write(tmp.resolve("a.zip").toFile(), zipBytes(StandardCharsets.UTF_8, "a.txt", "A"));
		assertThrows(IllegalArgumentException.class, () -> ZipUtils.unzip(zip, null));
		assertThrows(IOException.class, () -> ZipUtils.unzip(tmp.resolve("missing.zip").toFile(), dest));
		// destination is an existing regular file
		File regular = write(tmp.resolve("regular").toFile(), "x");
		IOException e = assertThrows(IOException.class, () -> ZipUtils.unzip(zip, regular));
		assertTrue(e.getMessage().startsWith("Cannot create directory"));
	}

	@Test
	public void listEntries_nullAndInvalid() throws IOException {
		assertEquals(Collections.emptyList(), ZipUtils.listEntries(null));
		assertEquals(Collections.emptyList(), ZipUtils.listEntries(null, GBK));
		File notZip = write(tmp.resolve("not.zip").toFile(), "not a zip");
		assertThrows(ZipException.class, () -> ZipUtils.listEntries(notZip));
	}

	// -------------------------------------------------------------------- gzip

	@Test
	public void gzipBytes_roundTripAndJdkCrossCheck() throws IOException {
		byte[] data = randomBytes(200000, 3L);
		byte[] gz = ZipUtils.gzip(data);
		assertEquals(0x1f, gz[0] & 0xff);
		assertEquals(0x8b, gz[1] & 0xff);
		try (InputStream in = new GZIPInputStream(new ByteArrayInputStream(gz))) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buf = new byte[4096];
			int n;
			while ((n = in.read(buf)) != -1) {
				out.write(buf, 0, n);
			}
			assertArrayEquals(data, out.toByteArray());
		}
		ByteArrayOutputStream jdk = new ByteArrayOutputStream();
		try (GZIPOutputStream out = new GZIPOutputStream(jdk)) {
			out.write(data);
		}
		assertArrayEquals(data, ZipUtils.ungzip(jdk.toByteArray()));
		assertArrayEquals(data, ZipUtils.ungzip(gz));
	}

	@Test
	public void gzip_concatenatedMembers() throws IOException {
		ByteArrayOutputStream both = new ByteArrayOutputStream();
		both.write(ZipUtils.gzip("hello "));
		both.write(ZipUtils.gzip("world"));
		assertEquals("hello world", ZipUtils.ungzipToString(both.toByteArray()));
	}

	@Test
	public void gzip_emptyAndNull() {
		byte[] gzEmpty = ZipUtils.gzip(new byte[0]);
		assertTrue(gzEmpty.length > 0);
		assertArrayEquals(new byte[0], ZipUtils.ungzip(gzEmpty));
		assertArrayEquals(new byte[0], ZipUtils.ungzip(new byte[0]));
		assertEquals("", ZipUtils.ungzipToString(new byte[0]));
		assertEquals("", ZipUtils.ungzipToString(ZipUtils.gzip("")));

		assertNull(ZipUtils.gzip((byte[]) null));
		assertNull(ZipUtils.ungzip((byte[]) null));
		assertNull(ZipUtils.gzip((String) null));
		assertNull(ZipUtils.gzip((String) null, GBK));
		assertNull(ZipUtils.ungzipToString(null));
		assertNull(ZipUtils.ungzipToString(null, GBK));
	}

	@Test
	public void gzipString_charsets() {
		String text = "hello 中文 " + new String(Character.toChars(0x1F600));
		assertEquals(text, ZipUtils.ungzipToString(ZipUtils.gzip(text)));
		assertArrayEquals(text.getBytes(StandardCharsets.UTF_8), ZipUtils.ungzip(ZipUtils.gzip(text)));
		assertEquals("中文", ZipUtils.ungzipToString(ZipUtils.gzip("中文", GBK), GBK));
		assertArrayEquals("中文".getBytes(GBK), ZipUtils.ungzip(ZipUtils.gzip("中文", GBK)));
		assertEquals(text, ZipUtils.ungzipToString(ZipUtils.gzip(text, null), null));
	}

	@Test
	public void ungzip_corruptDataThrows() {
		assertThrows(UncheckedIOException.class, () -> ZipUtils.ungzip("not gzip".getBytes(StandardCharsets.US_ASCII)));
		byte[] gz = ZipUtils.gzip(randomBytes(1000, 5L));
		byte[] truncated = Arrays.copyOf(gz, gz.length - 10);
		assertThrows(UncheckedIOException.class, () -> ZipUtils.ungzip(truncated));
		assertThrows(UncheckedIOException.class, () -> ZipUtils.ungzipToString(truncated));
	}

	@Test
	public void gzipStreams_doNotCloseCallerStreamsAndCountBytes() throws IOException {
		byte[] data = randomBytes(100000, 9L);
		TrackingInputStream in = new TrackingInputStream(data);
		TrackingOutputStream gz = new TrackingOutputStream();
		assertEquals(data.length, ZipUtils.gzip(in, gz));
		assertFalse(in.closed);
		assertFalse(gz.closed);

		TrackingInputStream gzIn = new TrackingInputStream(gz.toByteArray());
		TrackingOutputStream out = new TrackingOutputStream();
		assertEquals(data.length, ZipUtils.ungzip(gzIn, out));
		assertFalse(gzIn.closed);
		assertFalse(out.closed);
		assertArrayEquals(data, out.toByteArray());
	}

	@Test
	public void gzipStreams_invalidArguments() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = new ByteArrayInputStream(new byte[0]);
		assertThrows(IllegalArgumentException.class, () -> ZipUtils.gzip((InputStream) null, out));
		assertThrows(IllegalArgumentException.class, () -> ZipUtils.gzip(in, null));
		assertThrows(IllegalArgumentException.class, () -> ZipUtils.ungzip((InputStream) null, out));
		assertThrows(IllegalArgumentException.class, () -> ZipUtils.ungzip(in, null));
		assertThrows(IOException.class, () -> ZipUtils.ungzip(new ByteArrayInputStream(new byte[] { 1, 2, 3 }), out));
	}

	@Test
	public void zipOutputStream_closesOwnFileStreams() throws IOException {
		// on systems that lock open files deleting would fail; everywhere it proves the zip is complete
		File docs = sampleTree();
		File zip = tmp.resolve("c.zip").toFile();
		ZipUtils.zip(docs, zip);
		ZipUtils.unzip(zip, tmp.resolve("c").toFile());
		List<String> names = new ArrayList<String>(ZipUtils.listEntries(zip));
		assertEquals(7, names.size());
		assertTrue(zip.delete());
		try (FileOutputStream reopen = new FileOutputStream(zip)) {
			reopen.write(1);
		}
	}

	// ------------------------------------------------------- adversarial review

	@Test
	public void zipUnzipList_doNotLeakFileHandles_evenOnFailure() throws Exception {
		File docs = sampleTree();
		File zip = tmp.resolve("leak.zip").toFile();
		File evil = write(tmp.resolve("evil-leak.zip").toFile(), zipBytes(StandardCharsets.UTF_8, "ok.txt", "x", "../evil.txt", "pwned"));
		File corrupt = write(tmp.resolve("corrupt.zip").toFile(), "this is not a zip");
		IoTestSupport.assertNoFileHandleLeak(() -> {
			ZipUtils.zip(docs, zip);
			ZipUtils.unzip(zip, tmp.resolve("out").toFile());
			assertEquals(7, ZipUtils.listEntries(zip).size());
			assertThrows(IOException.class, () -> ZipUtils.unzip(evil, tmp.resolve("evil-out").toFile()));
			assertThrows(IOException.class, () -> ZipUtils.listEntries(corrupt));
			assertThrows(IllegalStateException.class, () -> ZipUtils.zip(docs, zip, f -> {
				if (f.getName().equals("b.bin")) {
					// fail in the middle of the archive, while the destination stream is open
					throw new IllegalStateException("boom");
				}
				return true;
			}));
		});
	}

	@Test
	public void unzip_rejectsAbsoluteEntryNamesOnEveryPlatform() throws IOException {
		for (String name : new String[] { "/abs/evil.txt", "\\abs\\evil.txt", "C:/evil.txt", "c:evil.txt", "/" }) {
			File dest = tmp.resolve("abs-" + Math.abs(name.hashCode())).toFile();
			byte[] data = zipBytes(StandardCharsets.UTF_8, name, "pwned");
			IOException e = assertThrows(IOException.class, () -> ZipUtils.unzip(new ByteArrayInputStream(data), dest), name);
			assertEquals("Zip entry is outside of the target directory: " + name, e.getMessage());
			assertEquals(0, dest.list().length, name);
		}
		// a name merely containing a colon later on is a normal relative name
		File dest = tmp.resolve("colon").toFile();
		ZipUtils.unzip(new ByteArrayInputStream(zipBytes(StandardCharsets.UTF_8, "ab:c.txt", "ok")), dest);
		assertTrue(new File(dest, "ab:c.txt").isFile());
	}

	@Test
	public void unzip_windowsSeparatorTraversalNeverEscapes() throws IOException {
		File work = tmp.resolve("win").toFile();
		File dest = new File(work, "dest");
		byte[] data = zipBytes(StandardCharsets.UTF_8, "..\\evil.txt", "pwned", "sub\\..\\..\\evil2.txt", "pwned");
		try {
			ZipUtils.unzip(new ByteArrayInputStream(data), dest);
			// on '/' file systems a backslash is an ordinary file name character
			assertEquals('/', File.separatorChar);
			assertTrue(new File(dest, "..\\evil.txt").isFile());
		} catch (IOException e) {
			// on Windows the backslash is a separator and the entry is rejected
			assertEquals('\\', File.separatorChar);
		}
		assertEquals(Collections.singletonList("dest"), Arrays.asList(work.list()));
	}

	@Test
	public void unzip_caseVariantOfDestDirNeverEscapes() throws IOException {
		File work = tmp.resolve("case").toFile();
		File dest = new File(work, "dest");
		byte[] data = zipBytes(StandardCharsets.UTF_8, "../DEST/x.txt", "pwned", "../Dest2/y.txt", "pwned");
		try {
			ZipUtils.unzip(new ByteArrayInputStream(data), dest);
		} catch (IOException expected) {
			// rejected: fine
		}
		// whatever the case sensitivity of the file system, nothing is created beside dest
		assertEquals(Collections.singletonList("dest"), Arrays.asList(work.list()));
	}

	@Test
	public void unzip_intoSymlinkedDestDirWritesIntoLinkTarget() throws IOException {
		File real = tmp.resolve("real").toFile();
		real.mkdirs();
		File link = tmp.resolve("link").toFile();
		try {
			Files.createSymbolicLink(link.toPath(), real.toPath());
		} catch (IOException | UnsupportedOperationException e) {
			assumeTrue(false, "symbolic links not supported");
		}
		ZipUtils.unzip(new ByteArrayInputStream(zipBytes(StandardCharsets.UTF_8, "a/b.txt", "ok")), link);
		assertTrue(new File(real, "a/b.txt").isFile());
		// and traversal is still judged against the real directory
		byte[] evil = zipBytes(StandardCharsets.UTF_8, "../escaped.txt", "pwned");
		assertThrows(IOException.class, () -> ZipUtils.unzip(new ByteArrayInputStream(evil), link));
		assertFalse(tmp.resolve("escaped.txt").toFile().exists());
	}

	@Test
	public void zip_destinationSameAsSourceFileFailsWithoutTruncatingIt() throws IOException {
		File file = write(tmp.resolve("same.txt").toFile(), "precious");
		IOException e = assertThrows(IOException.class, () -> ZipUtils.zip(file, file));
		assertTrue(e.getMessage().startsWith("Destination zip is the same as the source file"));
		assertEquals("precious", new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8));
		// also through a different spelling of the same path
		File other = new File(tmp.resolve("x").toFile(), "../same.txt");
		assertThrows(IOException.class, () -> ZipUtils.zip(new File[] { file }, other));
		assertEquals("precious", new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8));
	}


	private static byte[] zipOf(int entries, int bytesPerEntry) throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		try (ZipOutputStream out = new ZipOutputStream(bytes)) {
			byte[] zeros = new byte[bytesPerEntry];
			for (int i = 0; i < entries; i++) {
				out.putNextEntry(new ZipEntry("f" + i + ".bin"));
				out.write(zeros);
				out.closeEntry();
			}
		}
		return bytes.toByteArray();
	}

	@Test
	public void unzip_stopsWhenTheArchiveExpandsBeyondTheByteLimit(@TempDir File dest) throws IOException {
		byte[] bomb = zipOf(1, 10 * 1024 * 1024);
		assertTrue(bomb.length < 64 * 1024, "highly compressible payload");
		IOException e = assertThrows(IOException.class,
				() -> ZipUtils.unzip(new ByteArrayInputStream(bomb), dest, StandardCharsets.UTF_8, 1024 * 1024, 0));
		assertTrue(e.getMessage().contains("more than 1048576 bytes"), e.getMessage());
		File partial = new File(dest, "f0.bin");
		assertTrue(!partial.exists() || partial.length() <= 1024 * 1024);
	}

	@Test
	public void unzip_byteLimitIsCumulativeAcrossEntries(@TempDir File dest) throws IOException {
		byte[] zip = zipOf(3, 400);
		ZipUtils.unzip(new ByteArrayInputStream(zip), new File(dest, "ok"), null, 1200, 0);
		assertEquals(400, new File(dest, "ok/f2.bin").length());
		assertThrows(IOException.class, () -> ZipUtils.unzip(new ByteArrayInputStream(zip), new File(dest, "ko"), null, 1199, 0));
	}

	@Test
	public void unzip_stopsWhenTheArchiveHasTooManyEntries(@TempDir File dest) throws IOException {
		File zip = new File(dest, "many.zip");
		Files.write(zip.toPath(), zipOf(5, 1));
		IOException e = assertThrows(IOException.class, () -> ZipUtils.unzip(zip, new File(dest, "out"), StandardCharsets.UTF_8, 0, 4));
		assertTrue(e.getMessage().contains("more than 4 entries"), e.getMessage());
		ZipUtils.unzip(zip, new File(dest, "all"), StandardCharsets.UTF_8, 0, 5);
		assertEquals(5, new File(dest, "all").list().length);
	}

	@Test
	public void unzip_nonPositiveLimitsMeanUnlimited_andDefaultsAreGenerous(@TempDir File dest) throws IOException {
		ZipUtils.unzip(new ByteArrayInputStream(zipOf(3, 2048)), dest, null, 0, 0);
		assertEquals(3, dest.list().length);
		assertEquals(4L * 1024 * 1024 * 1024, ZipUtils.DEFAULT_MAX_UNZIP_BYTES);
		assertEquals(100000, ZipUtils.DEFAULT_MAX_UNZIP_ENTRIES);
	}
}
