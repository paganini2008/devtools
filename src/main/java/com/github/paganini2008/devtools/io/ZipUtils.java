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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
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
import java.util.Enumeration;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Zip and gzip utilities: compressing files and directories into zip archives,
 * safely extracting them (protected against Zip Slip) and gzip compression of
 * bytes, strings and streams.
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public abstract class ZipUtils {

	private static final int BUFFER_SIZE = 8192;

	private static final byte[] EMPTY_BYTES = new byte[0];

	/**
	 * Default limit of the total number of bytes {@code unzip} writes: 4 GB.
	 */
	public static final long DEFAULT_MAX_UNZIP_BYTES = 4L * 1024 * 1024 * 1024;

	/**
	 * Default limit of the number of entries {@code unzip} extracts: 100,000.
	 */
	public static final int DEFAULT_MAX_UNZIP_ENTRIES = 100000;

	// ------------------------------------------------------------------- zip

	/**
	 * Compresses a file or a directory into a new zip file (UTF-8 entry names).
	 * A file is stored under its own name; a directory is stored recursively
	 * under its own name, keeping relative paths and empty directories, for
	 * example zipping {@code /data/docs} produces entries {@code docs/},
	 * {@code docs/a.txt}, {@code docs/empty/}. Entry names always use '/'.
	 * Symbolically linked directories are not followed, and the destination zip
	 * itself is skipped if it lies inside the source directory.
	 *
	 * @throws IllegalArgumentException if {@code srcFileOrDir} or {@code destZip}
	 *                                  is null
	 * @throws IOException              if the source does not exist or an I/O
	 *                                  error occurs
	 */
	public static void zip(File srcFileOrDir, File destZip) throws IOException {
		zip(srcFileOrDir, destZip, null);
	}

	/**
	 * Same as {@link #zip(File, File)} but only includes files and directories
	 * below the source accepted by the filter (a rejected directory is skipped
	 * with all its content). A null filter accepts everything. The source itself
	 * is always included.
	 */
	public static void zip(File srcFileOrDir, File destZip, FileFilter filter) throws IOException {
		requireNonNull(srcFileOrDir, "Source file");
		zip(new File[] { srcFileOrDir }, destZip, filter);
	}

	/**
	 * Compresses several files or directories into one zip file, each stored
	 * at the top level under its own name (see {@link #zip(File, File)}).
	 *
	 * @throws IllegalArgumentException if {@code srcs}, one of its elements or
	 *                                  {@code destZip} is null
	 * @throws java.util.zip.ZipException if two sources have the same name
	 */
	public static void zip(File[] srcs, File destZip) throws IOException {
		zip(srcs, destZip, null);
	}

	/**
	 * Same as {@link #zip(File[], File)} with an optional filter.
	 */
	public static void zip(File[] srcs, File destZip, FileFilter filter) throws IOException {
		requireNonNull(srcs, "Source files");
		requireNonNull(destZip, "Destination zip");
		for (File src : srcs) {
			requireNonNull(src, "Source file");
			checkExists(src);
		}
		File parent = destZip.getAbsoluteFile().getParentFile();
		if (parent != null && !parent.isDirectory() && !parent.mkdirs()) {
			throw new IOException("Cannot create directory: " + parent);
		}
		File canonicalDest = destZip.getCanonicalFile();
		for (File src : srcs) {
			if (src.getCanonicalFile().equals(canonicalDest)) {
				// Opening the destination would truncate the source before it is read
				throw new IOException("Destination zip is the same as the source file: " + src);
			}
		}
		try (OutputStream out = new FileOutputStream(destZip)) {
			writeZip(srcs, out, filter, canonicalDest);
		}
	}

	/**
	 * Compresses a file or directory into the given stream (see
	 * {@link #zip(File, File)}). The zip is finished and the stream flushed, but
	 * the caller's stream is <b>not</b> closed.
	 *
	 * @throws IllegalArgumentException if {@code src} or {@code out} is null
	 */
	public static void zip(File src, OutputStream out) throws IOException {
		zip(src, out, null);
	}

	/**
	 * Same as {@link #zip(File, OutputStream)} with an optional filter.
	 */
	public static void zip(File src, OutputStream out, FileFilter filter) throws IOException {
		requireNonNull(src, "Source file");
		requireNonNull(out, "Output stream");
		checkExists(src);
		writeZip(new File[] { src }, out, filter, null);
	}

	private static void writeZip(File[] srcs, OutputStream out, FileFilter filter, File skip) throws IOException {
		try (ZipOutputStream zos = new ZipOutputStream(new NonClosingOutputStream(out), StandardCharsets.UTF_8)) {
			byte[] buffer = new byte[BUFFER_SIZE];
			for (File src : srcs) {
				addEntry(zos, src, src.getName(), filter, skip, buffer);
			}
			zos.finish();
		}
		out.flush();
	}

	private static void addEntry(ZipOutputStream zos, File file, String name, FileFilter filter, File skip, byte[] buffer)
			throws IOException {
		if (file.isDirectory()) {
			zos.putNextEntry(new ZipEntry(name + "/"));
			zos.closeEntry();
			File[] children = file.listFiles();
			if (children == null) {
				throw new IOException("Cannot list directory: " + file);
			}
			Arrays.sort(children);
			for (File child : children) {
				if (filter != null && !filter.accept(child)) {
					continue;
				}
				if (child.isDirectory() && Files.isSymbolicLink(child.toPath())) {
					continue;
				}
				if (skip != null && child.getCanonicalFile().equals(skip)) {
					continue;
				}
				addEntry(zos, child, name + "/" + child.getName(), filter, skip, buffer);
			}
		} else {
			ZipEntry entry = new ZipEntry(name);
			entry.setTime(file.lastModified());
			zos.putNextEntry(entry);
			try (InputStream in = new FileInputStream(file)) {
				copy(in, zos, buffer);
			}
			zos.closeEntry();
		}
	}

	// ----------------------------------------------------------------- unzip

	/**
	 * Extracts a zip file (UTF-8 entry names) into the destination directory,
	 * which is created if necessary, along with the parent directories of every
	 * entry. Existing files are overwritten.
	 * <p>
	 * Protected against <b>Zip Slip</b>: every target path is canonicalized and
	 * an entry that would end up outside {@code destDir} (such as
	 * {@code ../evil.txt} or an absolute path) causes an {@link IOException}
	 * before anything is written for that entry. Entries preceding it may
	 * already have been extracted inside {@code destDir}.
	 * <p>
	 * Protected against <b>zip bombs</b>: extraction stops with an
	 * {@link IOException} once more than {@link #DEFAULT_MAX_UNZIP_BYTES} bytes
	 * or {@link #DEFAULT_MAX_UNZIP_ENTRIES} entries would be extracted; use
	 * {@link #unzip(File, File, Charset, long, int)} to change the limits.
	 *
	 * @throws IllegalArgumentException if {@code zipFile} or {@code destDir} is
	 *                                  null
	 * @throws IOException              on a malicious entry name, an archive
	 *                                  exceeding the limits or an I/O error
	 */
	public static void unzip(File zipFile, File destDir) throws IOException {
		unzip(zipFile, destDir, StandardCharsets.UTF_8);
	}

	/**
	 * Same as {@link #unzip(File, File)} decoding entry names with the given
	 * charset (e.g. GBK for archives created by Chinese Windows tools). Entries
	 * flagged as UTF-8 in the archive are always decoded as UTF-8.
	 */
	public static void unzip(File zipFile, File destDir, Charset charset) throws IOException {
		unzip(zipFile, destDir, charset, DEFAULT_MAX_UNZIP_BYTES, DEFAULT_MAX_UNZIP_ENTRIES);
	}

	/**
	 * Same as {@link #unzip(File, File, Charset)} with custom zip bomb limits: at
	 * most {@code maxBytes} bytes in total and {@code maxEntries} entries are
	 * extracted. A limit &lt;= 0 means unlimited.
	 *
	 * @throws IOException if the archive exceeds a limit, on a malicious entry
	 *                     name or an I/O error
	 */
	public static void unzip(File zipFile, File destDir, Charset charset, long maxBytes, int maxEntries) throws IOException {
		requireNonNull(zipFile, "Zip file");
		try (InputStream in = new FileInputStream(zipFile)) {
			unzip(in, destDir, charset, maxBytes, maxEntries);
		}
	}

	/**
	 * Extracts a zip stream (UTF-8 entry names) into the destination directory
	 * with the same Zip Slip protection as {@link #unzip(File, File)}. The
	 * caller's stream is <b>not</b> closed.
	 *
	 * @throws IllegalArgumentException if {@code in} or {@code destDir} is null
	 */
	public static void unzip(InputStream in, File destDir) throws IOException {
		unzip(in, destDir, StandardCharsets.UTF_8);
	}

	/**
	 * Same as {@link #unzip(InputStream, File)} decoding entry names with the
	 * given charset (null means UTF-8).
	 */
	public static void unzip(InputStream in, File destDir, Charset charset) throws IOException {
		unzip(in, destDir, charset, DEFAULT_MAX_UNZIP_BYTES, DEFAULT_MAX_UNZIP_ENTRIES);
	}

	/**
	 * Same as {@link #unzip(InputStream, File, Charset)} with custom zip bomb
	 * limits: at most {@code maxBytes} bytes in total and {@code maxEntries}
	 * entries are extracted. A limit &lt;= 0 means unlimited. The caller's stream
	 * is <b>not</b> closed.
	 *
	 * @throws IOException if the archive exceeds a limit, on a malicious entry
	 *                     name or an I/O error
	 */
	public static void unzip(InputStream in, File destDir, Charset charset, long maxBytes, int maxEntries) throws IOException {
		requireNonNull(in, "Input stream");
		requireNonNull(destDir, "Destination directory");
		File canonicalDir = mkdirs(destDir).getCanonicalFile();
		Path root = canonicalDir.toPath();
		byte[] buffer = new byte[BUFFER_SIZE];
		try (ZipInputStream zis = new ZipInputStream(new NonClosingInputStream(in), charset != null ? charset : StandardCharsets.UTF_8)) {
			ZipEntry entry;
			long remaining = maxBytes > 0 ? maxBytes : Long.MAX_VALUE;
			int entries = 0;
			while ((entry = zis.getNextEntry()) != null) {
				if (maxEntries > 0 && ++entries > maxEntries) {
					throw new IOException("Zip archive has more than " + maxEntries + " entries");
				}
				String name = entry.getName();
				File target = new File(canonicalDir, name).getCanonicalFile();
				if (isAbsoluteName(name) || !target.toPath().startsWith(root) || target.equals(canonicalDir) && !entry.isDirectory()) {
					throw new IOException("Zip entry is outside of the target directory: " + name);
				}
				if (entry.isDirectory()) {
					mkdirs(target);
				} else {
					mkdirs(target.getParentFile());
					try (OutputStream out = new FileOutputStream(target)) {
						remaining -= copyLimited(zis, out, buffer, remaining, maxBytes);
					}
				}
				zis.closeEntry();
			}
		}
	}

	/**
	 * Returns the names of all entries of a zip file (UTF-8 entry names) in
	 * archive order, or an empty list for a null file.
	 *
	 * <pre>
	 * ZipUtils.listEntries(null)      = []
	 * ZipUtils.listEntries(docsZip)   = ["docs/", "docs/a.txt", "docs/empty/"]
	 * </pre>
	 */
	public static List<String> listEntries(File zipFile) throws IOException {
		return listEntries(zipFile, StandardCharsets.UTF_8);
	}

	/**
	 * Same as {@link #listEntries(File)} decoding entry names with the given
	 * charset (null means UTF-8).
	 */
	public static List<String> listEntries(File zipFile, Charset charset) throws IOException {
		if (zipFile == null) {
			return Collections.emptyList();
		}
		List<String> names = new ArrayList<String>();
		try (ZipFile zf = new ZipFile(zipFile, charset != null ? charset : StandardCharsets.UTF_8)) {
			Enumeration<? extends ZipEntry> entries = zf.entries();
			while (entries.hasMoreElements()) {
				names.add(entries.nextElement().getName());
			}
		}
		return names;
	}

	// ------------------------------------------------------------------ gzip

	/**
	 * Gzip-compresses a byte array. Returns null for null; an empty array
	 * produces a valid (non-empty) gzip stream of no data.
	 */
	public static byte[] gzip(byte[] data) {
		if (data == null) {
			return null;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream(Math.max(32, data.length / 2));
		try {
			gzip(new ByteArrayInputStream(data), out);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		return out.toByteArray();
	}

	/**
	 * Decompresses gzip data (concatenated gzip members are supported). Returns
	 * null for null and an empty array for an empty array.
	 *
	 * @throws UncheckedIOException if the data is not valid gzip
	 */
	public static byte[] ungzip(byte[] data) {
		if (data == null) {
			return null;
		}
		if (data.length == 0) {
			return EMPTY_BYTES;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream((int) Math.min(Integer.MAX_VALUE - 8, data.length * 2L));
		try {
			ungzip(new ByteArrayInputStream(data), out);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		return out.toByteArray();
	}

	/**
	 * Gzip-compresses a string encoded as UTF-8. Returns null for null.
	 *
	 * <pre>
	 * ZipUtils.ungzipToString(ZipUtils.gzip("hello 中文")) = "hello 中文"
	 * </pre>
	 */
	public static byte[] gzip(String str) {
		return gzip(str, StandardCharsets.UTF_8);
	}

	/**
	 * Gzip-compresses a string encoded with the given charset (null means
	 * UTF-8). Returns null for a null string.
	 */
	public static byte[] gzip(String str, Charset charset) {
		if (str == null) {
			return null;
		}
		return gzip(str.getBytes(charset != null ? charset : StandardCharsets.UTF_8));
	}

	/**
	 * Decompresses gzip data into a UTF-8 string. Returns null for null and ""
	 * for an empty array.
	 *
	 * @throws UncheckedIOException if the data is not valid gzip
	 */
	public static String ungzipToString(byte[] data) {
		return ungzipToString(data, StandardCharsets.UTF_8);
	}

	/**
	 * Decompresses gzip data into a string decoded with the given charset (null
	 * means UTF-8).
	 */
	public static String ungzipToString(byte[] data, Charset charset) {
		byte[] bytes = ungzip(data);
		return bytes != null ? new String(bytes, charset != null ? charset : StandardCharsets.UTF_8) : null;
	}

	/**
	 * Gzip-compresses everything read from {@code in} into {@code out}. Neither
	 * caller stream is closed; the gzip trailer is written and {@code out} is
	 * flushed.
	 *
	 * @return the number of uncompressed bytes read
	 * @throws IllegalArgumentException if a stream is null
	 */
	public static long gzip(InputStream in, OutputStream out) throws IOException {
		requireNonNull(in, "Input stream");
		requireNonNull(out, "Output stream");
		long count;
		try (GZIPOutputStream gzip = new GZIPOutputStream(new NonClosingOutputStream(out), BUFFER_SIZE)) {
			count = copy(in, gzip, new byte[BUFFER_SIZE]);
			gzip.finish();
		}
		out.flush();
		return count;
	}

	/**
	 * Decompresses gzip data read from {@code in} into {@code out}. Neither
	 * caller stream is closed.
	 *
	 * @return the number of uncompressed bytes written
	 * @throws IllegalArgumentException if a stream is null
	 * @throws IOException              if the data is not valid gzip
	 */
	public static long ungzip(InputStream in, OutputStream out) throws IOException {
		requireNonNull(in, "Input stream");
		requireNonNull(out, "Output stream");
		long count;
		try (GZIPInputStream gzip = new GZIPInputStream(new NonClosingInputStream(in), BUFFER_SIZE)) {
			count = copy(gzip, out, new byte[BUFFER_SIZE]);
		}
		out.flush();
		return count;
	}

	// --------------------------------------------------------------- helpers

	private static long copy(InputStream in, OutputStream out, byte[] buffer) throws IOException {
		long count = 0;
		int n;
		while ((n = in.read(buffer)) != -1) {
			out.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

	/**
	 * An absolute entry name ("/etc/x", "\\x" or "C:\\x") is never produced by a
	 * well-behaved tool, it is rejected on every platform.
	 */
	private static long copyLimited(InputStream in, OutputStream out, byte[] buffer, long remaining, long maxBytes) throws IOException {
		long total = 0;
		int n;
		while ((n = in.read(buffer)) != -1) {
			if (n > remaining - total) {
				throw new IOException("Zip archive expands to more than " + maxBytes + " bytes");
			}
			out.write(buffer, 0, n);
			total += n;
		}
		return total;
	}

	private static boolean isAbsoluteName(String name) {
		return name.startsWith("/") || name.startsWith("\\")
				|| (name.length() > 1 && name.charAt(1) == ':' && Character.isLetter(name.charAt(0)));
	}

	private static File mkdirs(File dir) throws IOException {
		if (!dir.isDirectory() && !dir.mkdirs()) {
			throw new IOException("Cannot create directory: " + dir);
		}
		return dir;
	}

	private static void checkExists(File file) throws IOException {
		if (!file.exists()) {
			throw new IOException("File not found: " + file);
		}
	}

	private static void requireNonNull(Object value, String name) {
		if (value == null) {
			throw new IllegalArgumentException(name + " must not be null");
		}
	}

	/**
	 * Shields the caller's stream from being closed by a wrapping stream.
	 */
	private static final class NonClosingInputStream extends FilterInputStream {

		NonClosingInputStream(InputStream in) {
			super(in);
		}

		@Override
		public void close() {
		}
	}

	/**
	 * Shields the caller's stream from being closed by a wrapping stream.
	 */
	private static final class NonClosingOutputStream extends FilterOutputStream {

		NonClosingOutputStream(OutputStream out) {
			super(out);
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			out.write(b, off, len);
		}

		@Override
		public void close() throws IOException {
			flush();
		}
	}

}
