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

import static com.github.paganini2008.devtools.io.IOUtils.closeQuietly;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import com.github.paganini2008.devtools.ArrayUtils;
import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.CharsetUtils;
import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.io.comparator.FileSorter;
import com.github.paganini2008.devtools.math.BigDecimalUtils;

/**
 * File manipulation utilities: reading, writing, copying, moving, deleting and scanning files and directories.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public abstract class FileUtils {

	public static final File[] EMPTY_ARRAY = new File[0];

	public static final long KB = 1024L;

	public static final long MB = KB * KB;

	public static final long GB = KB * MB;

	public static final long TB = KB * GB;

	public static final long PB = KB * TB;

	public static final BigInteger EB = BigInteger.valueOf(KB).multiply(BigInteger.valueOf(PB));

	public static final BigInteger ZB = BigInteger.valueOf(KB).multiply(EB);

	public static final BigInteger YB = BigInteger.valueOf(KB).multiply(ZB);

	public static final long FILE_COPY_BUFFER_SIZE = 30 * MB;

	/**
	 * Builds a file from a parent directory and child names.
	 *
	 * <pre>
	 * FileUtils.getFile(new File("/tmp"), "a", "b") = /tmp/a/b
	 * </pre>
	 *
	 * @throws IllegalArgumentException if the directory or the names are {@code null}
	 */
	public static File getFile(File directory, String... names) {
		Assert.isNull(directory, "Destination directory must not be null.");
		Assert.isNull(names, "Names must not be null.");
		File file = directory;
		for (String name : names) {
			file = new File(file, name);
		}
		return file;
	}

	/**
	 * Builds a file from path names; returns {@code null} if no name is given.
	 *
	 * @throws IllegalArgumentException if the names are {@code null}
	 */
	public static File getFile(String... names) {
		Assert.isNull(names, "Names must not be null.");
		File file = null;
		for (String name : names) {
			if (file == null) {
				file = new File(name);
			} else {
				file = new File(file, name);
			}
		}
		return file;
	}

	public static String getTempDirectoryPath() {
		return System.getProperty("java.io.tmpdir");
	}

	public static File getTempDirectory() {
		return new File(getTempDirectoryPath());
	}

	public static String getUserHomePath() {
		return System.getProperty("user.home");
	}

	public static File getUserHome() {
		return new File(getUserHomePath());
	}

	public static String getUserDirectoryPath() {
		return System.getProperty("user.dir");
	}

	public static File getUserDirectory() {
		return new File(getUserDirectoryPath());
	}

	/**
	 * Returns the file name without extension, or {@code ""} for {@code null}.
	 *
	 * <pre>
	 * FileUtils.getBaseName(new File("dir/report.txt")) = "report"
	 * FileUtils.getBaseName(null)                       = ""
	 * </pre>
	 */
	public static String getBaseName(File file) {
		return file != null ? PathUtils.getBaseName(file.getName()) : "";
	}

	/**
	 * Returns the extension of the file name, or {@code ""} for {@code null}.
	 *
	 * <pre>
	 * FileUtils.getExtension(new File("dir/report.txt")) = "txt"
	 * FileUtils.getExtension(null)                       = ""
	 * </pre>
	 */
	public static String getExtension(File file) {
		return file != null ? PathUtils.getExtension(file.getName()) : "";
	}

	private static synchronized void canRead(File file) throws IOException {
		Assert.isNull(file, "Source file must not be null.");
		FileAssert.notExisted(file);
		FileAssert.isDirectory(file);
		FileAssert.cannotRead(file);
	}

	private static synchronized void canScan(File directory) throws IOException {
		Assert.isNull(directory, "Source directory must not be null.");
		FileAssert.notExisted(directory);
		FileAssert.isFile(directory);
		FileAssert.cannotRead(directory);
	}

	private static synchronized void canWrite(File file) throws IOException {
		Assert.isNull(file, "Destination file must not be null.");
		FileAssert.isDirectory(file);
		touch(file);
		FileAssert.cannotWrite(file);
	}

	private static synchronized void canChange(File directory) throws IOException {
		Assert.isNull(directory, "Destination directory must not be null.");
		FileAssert.isFile(directory);
		mkdirs(directory);
		FileAssert.cannotWrite(directory);
	}

	private static boolean isSameOrSubPath(File parent, File child) throws IOException {
		String parentPath = parent.getCanonicalPath();
		String childPath = child.getCanonicalPath();
		if (childPath.equals(parentPath)) {
			return true;
		}
		if (!parentPath.endsWith(File.separator)) {
			parentPath += File.separator;
		}
		return childPath.startsWith(parentPath);
	}

	/**
	 * Creates a file for every name under the parent; {@code null} names stay {@code null}. Returns {@code null} for a
	 * {@code null} array.
	 */
	public static File[] getFiles(File parent, String... files) {
		if (files == null) {
			return null;
		}
		int l = files.length;
		File[] array = new File[l];
		for (int i = 0; i < l; i++) {
			array[i] = files[i] != null ? new File(parent, files[i]) : null;
		}
		return array;
	}

	/**
	 * Creates a file for every name; {@code null} names stay {@code null}. Returns {@code null} for a {@code null} array.
	 */
	public static File[] getFiles(String... files) {
		if (files == null) {
			return null;
		}
		int l = files.length;
		File[] array = new File[l];
		for (int i = 0; i < l; i++) {
			array[i] = files[i] != null ? new File(files[i]) : null;
		}
		return array;
	}

	/**
	 * Creates the file (and its parent directories) if it does not exist, otherwise updates its last modified time.
	 * {@code null} is ignored.
	 *
	 * @throws IOException if the file cannot be created
	 */
	public static void touch(File file) throws IOException {
		if (file != null) {
			if (file.exists()) {
				file.setLastModified(System.currentTimeMillis());
			} else {
				File parent = file.getParentFile();
				mkdirs(parent);
				if (!file.createNewFile() || !file.isFile()) {
					throw new IOException("File '" + file + "' cannot be created.");
				}
			}
		}
	}

	/**
	 * Creates the directory and all missing parents.
	 *
	 * @return {@code true} if the directory already existed (or is {@code null}), {@code false} if it was created
	 * @throws IOException if the directory cannot be created
	 */
	public static boolean mkdirs(File dir) throws IOException {
		if (dir != null && !dir.exists()) {
			if (!dir.mkdirs() && !dir.isDirectory()) {
				throw new IOException("Directory '" + dir + "' cannot be created.");
			}
			return false;
		}
		return true;
	}

	public static void mkdirs(String dir) throws IOException {
		mkdirs(new File(dir));
	}

	public static void touch(String file) throws IOException {
		touch(new File(file));
	}

	/**
	 * Opens an existing readable file as a read-write {@link RandomAccessFile}.
	 *
	 * @throws java.io.FileNotFoundException if the file does not exist
	 * @throws IOException if the file is a directory or not readable
	 */
	public static RandomAccessFile getRandomAccessFile(File file) throws IOException {
		canRead(file);
		return new RandomAccessFile(file, "rw");
	}

	public static FileInputStream openInputStream(String file) throws IOException {
		return openInputStream(new File(file));
	}

	/**
	 * Opens an input stream for an existing readable file. The caller must close the stream.
	 *
	 * @throws IllegalArgumentException if the file is {@code null}
	 * @throws java.io.FileNotFoundException if the file does not exist
	 * @throws IOException if the file is a directory or not readable
	 */
	public static FileInputStream openInputStream(File file) throws IOException {
		canRead(file);
		return new FileInputStream(file);
	}

	public static BufferedReader getBufferedReader(String filePath, String charset) throws IOException {
		return getBufferedReader(new File(filePath), charset);
	}

	/**
	 * Opens a buffered reader decoding the file with the charset (the platform default if {@code null}).
	 */
	public static BufferedReader getBufferedReader(File file, String charset) throws IOException {
		return IOUtils.getBufferedReader(openInputStream(file), CharsetUtils.toCharset(charset));
	}

	/**
	 * Same as {@link #getBufferedReader(File, String)} with the buffer size.
	 *
	 * @throws IllegalArgumentException if {@code bufferSize <= 0} (the file is not left open)
	 */
	public static BufferedReader getBufferedReader(File file, String charset, int bufferSize) throws IOException {
		FileInputStream in = openInputStream(file);
		try {
			return IOUtils.getBufferedReader(in, CharsetUtils.toCharset(charset), bufferSize);
		} catch (RuntimeException e) {
			closeQuietly(in);
			throw e;
		}
	}

	/**
	 * Opens a line number reader decoding the file with the charset (the platform default if {@code null}).
	 */
	public static LineNumberReader getLineNumberReader(File file, Charset charset) throws IOException {
		return IOUtils.getLineNumberReader(openInputStream(file), CharsetUtils.toCharset(charset));
	}

	public static FileOutputStream openOutputStream(String file) throws IOException {
		return openOutputStream(new File(file));
	}

	public static FileOutputStream openOutputStream(File file) throws IOException {
		return openOutputStream(file, false);
	}

	public static FileOutputStream openOutputStream(String file, boolean append) throws IOException {
		return openOutputStream(new File(file), append);
	}

	/**
	 * Opens an output stream to the file, creating the file and its parent directories if necessary. The caller must close
	 * the stream.
	 *
	 * @throws IllegalArgumentException if the file is {@code null}
	 * @throws IOException if the file is a directory or not writable
	 */
	public static FileOutputStream openOutputStream(File file, boolean append) throws IOException {
		canWrite(file);
		return new FileOutputStream(file, append);
	}

	public static BufferedWriter getBufferedWriter(String filePath, Charset charset) throws IOException {
		return getBufferedWriter(new File(filePath), charset);
	}

	public static BufferedWriter getBufferedWriter(File file, Charset charset) throws IOException {
		return getBufferedWriter(file, false, charset);
	}

	/**
	 * Opens a buffered writer encoding with the charset, creating the file and its parent directories if necessary.
	 */
	public static BufferedWriter getBufferedWriter(File file, boolean append, Charset charset) throws IOException {
		FileOutputStream fos = openOutputStream(file, append);
		return IOUtils.getBufferedWriter(fos, charset);
	}

	/**
	 * Formats a byte count with 2 decimal places, see {@link #formatSize(BigInteger, int)}.
	 */
	public static String formatSize(long size) {
		return formatSize(size, 2);
	}

	public static String formatSize(long size, int scale) {
		return formatSize(BigInteger.valueOf(size), scale);
	}

	public static String formatSize(BigInteger size) {
		return formatSize(size, 2);
	}

	/**
	 * Formats a byte count with the largest binary unit (KB = 1024 bytes ... YB) whose value, rounded half up to
	 * {@code scale} decimal places, is at least 1.
	 *
	 * <pre>
	 * FileUtils.formatSize(1000)      = "1000.00 bytes"
	 * FileUtils.formatSize(1024)      = "1.00 KB"
	 * FileUtils.formatSize(1023)      = "1.00 KB"
	 * FileUtils.formatSize(1536, 1)   = "1.5 KB"
	 * </pre>
	 *
	 * @throws IllegalArgumentException if the size is {@code null}
	 */
	public static String formatSize(BigInteger size, int scale) {
		Assert.isNull(size, "Size must not be null.");
		BigDecimal tmp;
		String displaySize;
		if ((tmp = BigDecimalUtils.divide(size, YB, scale, RoundingMode.HALF_UP)).toBigInteger().compareTo(BigInteger.ZERO) > 0) {
			displaySize = String.valueOf(tmp) + " YB";
		} else if ((tmp = BigDecimalUtils.divide(size, ZB, scale, RoundingMode.HALF_UP)).toBigInteger().compareTo(BigInteger.ZERO) > 0) {
			displaySize = String.valueOf(tmp) + " ZB";
		} else if ((tmp = BigDecimalUtils.divide(size, EB, scale, RoundingMode.HALF_UP)).toBigInteger().compareTo(BigInteger.ZERO) > 0) {
			displaySize = String.valueOf(tmp) + " EB";
		} else if ((tmp = BigDecimalUtils.divide(size, PB, scale, RoundingMode.HALF_UP)).toBigInteger().compareTo(BigInteger.ZERO) > 0) {
			displaySize = String.valueOf(tmp) + " PB";
		} else if ((tmp = BigDecimalUtils.divide(size, TB, scale, RoundingMode.HALF_UP)).toBigInteger().compareTo(BigInteger.ZERO) > 0) {
			displaySize = String.valueOf(tmp) + " TB";
		} else if ((tmp = BigDecimalUtils.divide(size, GB, scale, RoundingMode.HALF_UP)).toBigInteger().compareTo(BigInteger.ZERO) > 0) {
			displaySize = String.valueOf(tmp) + " GB";
		} else if ((tmp = BigDecimalUtils.divide(size, MB, scale, RoundingMode.HALF_UP)).toBigInteger().compareTo(BigInteger.ZERO) > 0) {
			displaySize = String.valueOf(tmp) + " MB";
		} else if ((tmp = BigDecimalUtils.divide(size, KB, scale, RoundingMode.HALF_UP)).toBigInteger().compareTo(BigInteger.ZERO) > 0) {
			displaySize = String.valueOf(tmp) + " KB";
		} else {
			tmp = BigDecimalUtils.setScale(size, scale, RoundingMode.HALF_UP);
			displaySize = String.valueOf(tmp) + " bytes";
		}
		return displaySize;
	}

	/**
	 * Tests whether the file was modified after the reference file.
	 *
	 * @throws java.io.FileNotFoundException if either file does not exist
	 */
	public static boolean isFileNewer(File file, File reference) throws IOException {
		FileAssert.notExisted(reference);
		return isFileNewer(file, reference.lastModified());
	}

	public static boolean isFileNewer(File file, Date referenceDate) throws IOException {
		Assert.isNull(referenceDate, "Unspecified referenceDate.");
		return isFileNewer(file, referenceDate.getTime());
	}

	/**
	 * Tests whether the file was modified after the given time.
	 *
	 * @throws java.io.FileNotFoundException if the file does not exist
	 */
	public static boolean isFileNewer(File file, long timeMillis) throws IOException {
		FileAssert.notExisted(file);
		return file.lastModified() > timeMillis;
	}

	/**
	 * Tests whether the file was modified before the reference file.
	 *
	 * @throws java.io.FileNotFoundException if either file does not exist
	 */
	public static boolean isFileOlder(File file, File reference) throws IOException {
		FileAssert.notExisted(reference);
		return isFileOlder(file, reference.lastModified());
	}

	public static boolean isFileOlder(File file, Date referenceDate) throws IOException {
		Assert.isNull(referenceDate, "Unspecified referenceDate.");
		return isFileOlder(file, referenceDate.getTime());
	}

	/**
	 * Tests whether the file was modified before the given time.
	 *
	 * @throws java.io.FileNotFoundException if the file does not exist
	 */
	public static boolean isFileOlder(File file, long timeMillis) throws IOException {
		FileAssert.notExisted(file);
		return file.lastModified() < timeMillis;
	}

	/**
	 * Tests whether the file itself is a symbolic link.
	 *
	 * @throws IllegalArgumentException if the file is {@code null}
	 */
	public static boolean isSymlink(File file) throws IOException {
		Assert.isNull(file, "File must not be null.");
		return Files.isSymbolicLink(file.toPath());
	}

	/**
	 * Deletes the directory recursively. Symbolic links are deleted without deleting their targets.
	 *
	 * @throws IOException if the argument is a file or something cannot be deleted
	 */
	public static void deleteDirectory(File directory) throws IOException {
		canChange(directory);
		if (!isSymlink(directory)) {
			cleanDirectory(directory);
		}
		if (!directory.delete()) {
			throw new IOException("Unable to delete directory " + directory);
		}
	}

	/**
	 * Deletes everything inside the directory but keeps the directory itself.
	 *
	 * @throws IOException if the argument is a file or something cannot be deleted
	 */
	public static void cleanDirectory(File directory) throws IOException {
		canChange(directory);
		File[] files = directory.listFiles();
		if (files != null) {
			for (File file : files) {
				deleteFile(file);
			}
		}
	}

	/**
	 * Deletes the file, or the directory recursively.
	 *
	 * @throws java.io.FileNotFoundException if the file does not exist
	 * @throws IOException if something cannot be deleted
	 */
	public static void deleteFile(File file) throws IOException {
		Assert.isNull(file, "Destination file must not be null.");
		FileAssert.notExisted(file);
		if (file.isDirectory()) {
			deleteDirectory(file);
		} else {
			if (!file.delete()) {
				throw new IOException("Unable to delete file: " + file);
			}
		}
	}

	/**
	 * Schedules the file, or the directory recursively, for deletion when the JVM exits.
	 *
	 * @throws java.io.FileNotFoundException if the file does not exist
	 */
	public static void deleteFileOnExit(File file) throws IOException {
		Assert.isNull(file, "Destination file must not be null.");
		FileAssert.notExisted(file);
		if (file.isDirectory()) {
			deleteDirectoryOnExit(file);
		} else {
			file.deleteOnExit();
		}
	}

	private static void deleteDirectoryOnExit(File directory) throws IOException {
		directory.deleteOnExit();
		if (!isSymlink(directory)) {
			cleanDirectoryOnExit(directory);
		}
	}

	private static void cleanDirectoryOnExit(File directory) throws IOException {
		File[] files = directory.listFiles();
		if (files != null) {
			for (File file : files) {
				deleteFileOnExit(file);
			}
		}
	}

	/**
	 * Reads the whole file content decoded with the charset (the platform default if {@code null}), keeping the line
	 * terminators exactly as they are.
	 *
	 * <pre>
	 * file content "a\r\nb" -&gt; "a\r\nb"
	 * empty file            -&gt; ""
	 * </pre>
	 *
	 * @throws java.io.FileNotFoundException if the file does not exist
	 */
	public static String readFileToString(File file, Charset charset) throws IOException {
		Assert.isNull(file, "File must not be null.");
		try (InputStream in = openInputStream(file)) {
			return IOUtils.toString(in, charset);
		}
	}

	public static String readFileToString(String filePath, Charset charset) throws IOException {
		return readFileToString(new File(filePath), charset);
	}

	/**
	 * Null safe {@link File#exists()}.
	 */
	public static boolean exists(File file) {
		return file != null ? file.exists() : false;
	}

	public static boolean notExists(File file) {
		return !exists(file);
	}

	public static boolean notExists(String file) {
		return !exists(file);
	}

	/**
	 * Null safe test whether the path exists.
	 */
	public static boolean exists(String file) {
		return file != null ? new File(file).exists() : false;
	}

	public static boolean isAvailable(String file) {
		return file != null ? isAvailable(new File(file)) : false;
	}

	/**
	 * Tests whether the file exists and is readable and writable; {@code false} for {@code null}.
	 */
	public static boolean isAvailable(File f) {
		return f != null ? (f.exists() && f.canRead() && f.canWrite()) : false;
	}

	public static boolean isNotAvailable(String file) {
		return !isAvailable(file);
	}

	public static boolean isNotAvailable(File f) {
		return !isAvailable(f);
	}

	/**
	 * Reads all lines of a UTF-8 file.
	 */
	public static List<String> readLines(File file) throws IOException {
		return readLines(file, CharsetUtils.UTF_8);
	}

	/**
	 * Reads all lines of the file decoded with the charset.
	 *
	 * @throws IllegalArgumentException if the file is {@code null}
	 */
	public static List<String> readLines(File file, Charset charset) throws IOException {
		Assert.isNull(file, "File must not be null.");
		List<String> list = new ArrayList<String>();
		copyFile(file, list, charset);
		return list;
	}

	/**
	 * Returns the line number reached after reading the whole file with a {@link LineNumberReader}, i.e. the number of line
	 * terminators (newer JDKs also count a last line without terminator).
	 */
	public static int getLineNumber(File file, Charset charset) throws IOException {
		try (LineNumberReader reader = getLineNumberReader(file, charset)) {
			reader.skip(file.length());
			return reader.getLineNumber();
		}
	}

	private static int countLines(File file, Charset charset) throws IOException {
		try (BufferedReader reader = IOUtils.getBufferedReader(openInputStream(file), charset)) {
			int lines = 0;
			while (reader.readLine() != null) {
				lines++;
			}
			return lines;
		}
	}

	public static void split(File file, int line, File output, String template) throws IOException {
		split(file, null, line, output, template);
	}

	/**
	 * Splits the file into files of {@code line} lines each, written to the output directory. The file names are created from
	 * the template, in which {@code #0} is replaced with the 1-based part number, plus the extension of the source file.
	 *
	 * <pre>
	 * FileUtils.split(new File("data.txt"), UTF_8, 2, dir, "part-#0") creates part-1.txt, part-2.txt, ...
	 * </pre>
	 *
	 * @throws IllegalArgumentException if {@code line <= 0}, the output directory is {@code null} or the template is blank
	 */
	public static void split(File file, Charset charset, int line, File output, String template) throws IOException {
		if (line <= 0) {
			throw new IllegalArgumentException("Lines per file must be greater than 0: " + line);
		}
		Assert.isNull(output, "Output directory must not be null.");
		Assert.hasNoText(template, "Template must not be blank.");
		int rows = countLines(file, charset);
		int count = rows % line == 0 ? rows / line : rows / line + 1;
		String extension = getExtension(file);
		LineNumberReader reader = null;
		try {
			reader = getLineNumberReader(file, charset);
			int no = 1, start = 1;
			StringBuilder content = null;
			String name;
			Object[] array = new Object[1];
			while (no <= count) {
				content = new StringBuilder();
				IOUtils.rangeCopyLines(reader, start, line, content);
				array[0] = no++;
				name = StringUtils.parseText(template, "#", array) + "." + extension;
				writeFile(content, new File(output, name), false, charset);
				start += line;
			}
		} finally {
			closeQuietly(reader);
		}
	}

	/**
	 * Concatenates the contents (as returned by {@link #readFileToString(File, Charset)}) of the files into the output file,
	 * separated by {@link IOUtils#NEWLINE}. A {@code null} array creates an empty file.
	 */
	public static void mergeTo(File[] files, File outputFile, Charset charset) throws IOException {
		StringBuilder content = new StringBuilder();
		for (int i = 0, l = (files != null ? files.length : 0); i < l; i++) {
			content.append(readFileToString(files[i], charset));
			if (i != l - 1) {
				content.append(IOUtils.NEWLINE);
			}
		}
		writeFile(content, outputFile, false, charset);
	}

	/**
	 * Reads all bytes of the file.
	 */
	public static byte[] readFileToByteArray(File file) throws IOException {
		try (FileInputStream in = openInputStream(file)) {
			return IOUtils.toByteArray(in);
		}
	}

	/**
	 * Reads all characters of the file decoded with the charset.
	 */
	public static char[] readFileToCharArray(File file, Charset charset) throws IOException {
		Assert.isNull(file, "File must not be null.");
		try (InputStream in = openInputStream(file)) {
			return IOUtils.toCharArray(in, charset);
		}
	}

	/**
	 * Writes the bytes to the file, creating the file and its parent directories if necessary.
	 *
	 * @throws IllegalArgumentException if an argument is {@code null}
	 */
	public static void writeFile(byte[] bytes, File file, boolean append) throws IOException {
		Assert.isNull(bytes, "Bytes must not be null.");
		Assert.isNull(file, "Destination file must not be null.");
		try (OutputStream os = openOutputStream(file, append)) {
			os.write(bytes);
		}
	}

	/**
	 * Copies the stream to the file, creating the file and its parent directories if necessary. The input stream is not
	 * closed.
	 *
	 * @throws IllegalArgumentException if an argument is {@code null}
	 */
	public static void writeFile(InputStream in, File file, boolean append) throws IOException {
		Assert.isNull(in, "InputStream must not be null.");
		Assert.isNull(file, "Destination file must not be null.");
		try (OutputStream os = openOutputStream(file, append)) {
			IOUtils.copy(in, os);
		}
	}

	/**
	 * Writes the characters of the reader encoded with the charset to the file. The reader is not closed.
	 *
	 * @throws IllegalArgumentException if an argument is {@code null}
	 */
	public static void writeFile(Reader reader, File file, boolean append, Charset charset) throws IOException {
		Assert.isNull(reader, "Content must not be null.");
		Assert.isNull(file, "Destination file must not be null.");
		try (FileOutputStream fos = openOutputStream(file, append)) {
			IOUtils.copy(reader, fos, charset);
		}
	}

	/**
	 * Writes the characters encoded with the charset to the file.
	 *
	 * @throws IllegalArgumentException if an argument is {@code null}
	 */
	public static void writeFile(char[] content, File file, boolean append, Charset charset) throws IOException {
		Assert.isNull(content, "Content must not be null.");
		Assert.isNull(file, "Destination file must not be null.");
		try (FileOutputStream fos = openOutputStream(file, append)) {
			IOUtils.copy(content, fos, charset);
		}
	}

	/**
	 * Writes the text encoded with the charset to the file.
	 *
	 * @throws IllegalArgumentException if an argument is {@code null}
	 */
	public static void writeFile(CharSequence content, File file, boolean append, Charset charset) throws IOException {
		Assert.isNull(content, "Content must not be null.");
		Assert.isNull(file, "Destination file must not be null.");
		try (FileOutputStream fos = openOutputStream(file, append)) {
			IOUtils.copy(content, fos, charset);
		}
	}

	/**
	 * Writes every string as a line encoded with the charset to the file.
	 *
	 * @throws IllegalArgumentException if an argument is {@code null}
	 */
	public static void writeFile(Collection<String> collection, File file, boolean append, Charset charset) throws IOException {
		Assert.isNull(collection, "Content must not be null.");
		Assert.isNull(file, "Destination file must not be null.");
		try (FileOutputStream fos = openOutputStream(file, append)) {
			IOUtils.writeLines(collection, fos, charset);
		}
	}

	/**
	 * Moves the file into the directory, keeping its name.
	 */
	public static void moveFileToDirectory(File srcFile, File destDir) throws IOException {
		Assert.isNull(srcFile, "Unspecified source file.");
		Assert.isNull(destDir, "Unspecified destination directory.");
		moveFile(srcFile, new File(destDir, srcFile.getName()));
	}

	/**
	 * Moves the file or directory into the directory, keeping its name.
	 */
	public static void moveToDirectory(File src, File destDir, boolean createDestDir) throws IOException {
		Assert.isNull(src, "Unspecified source file.");
		Assert.isNull(destDir, "Unspecified destination directory.");
		if (src.isDirectory()) {
			moveDirectoryToDirectory(src, destDir);
		} else {
			moveFileToDirectory(src, destDir);
		}
	}

	/**
	 * Moves the directory, by renaming it or by copying and deleting it.
	 *
	 * @throws IOException if the destination is the source itself or one of its sub directories, or moving fails
	 */
	public static void moveDirectory(File srcDir, File destDir) throws IOException {
		canScan(srcDir);
		Assert.isNull(destDir, "Destination directory must not be null.");
		if (isSameOrSubPath(srcDir, destDir)) {
			throw new IOException("Cannot move directory: " + srcDir + " to a subdirectory of itself: " + destDir);
		}
		canChange(destDir);
		if (!srcDir.renameTo(destDir)) {
			copyDirectory(srcDir, destDir);
			deleteDirectory(srcDir);
			if (srcDir.exists()) {
				throw new IOException("Failed to delete original directory '" + srcDir + "' after copy to '" + destDir + "'");
			}
		}
	}

	/**
	 * Moves the directory into the destination directory, keeping its name.
	 */
	public static void moveDirectoryToDirectory(File srcDir, File destDir) throws IOException {
		Assert.isNull(srcDir, "Unspecified source directory");
		Assert.isNull(destDir, "Unspecified destination directory");
		moveDirectory(srcDir, new File(destDir, srcDir.getName()));
	}

	/**
	 * Moves the file, by renaming it or by copying and deleting it. Parent directories of the destination are created.
	 *
	 * @throws java.io.FileNotFoundException if the source file does not exist
	 */
	public static void moveFile(File srcFile, File destFile) throws IOException {
		canRead(srcFile);
		canWrite(destFile);
		if (!srcFile.renameTo(destFile)) {
			doCopyFile(srcFile, destFile);
			if (!srcFile.delete()) {
				deleteFile(destFile);
				throw new IOException("Failed to delete original file '" + srcFile + "' after copy to '" + destFile + "'");
			}
		}
	}

	/**
	 * Copies the file into the directory (created if necessary), keeping its name.
	 *
	 * @throws java.io.FileNotFoundException if the source file does not exist
	 */
	public static void copyFileToDirectory(File srcFile, File destDir) throws IOException {
		canRead(srcFile);
		canChange(destDir);
		File destFile = new File(destDir, srcFile.getName());
		doCopyFile(srcFile, destFile);
	}

	/**
	 * Copies the file. Parent directories of the destination are created.
	 *
	 * @throws java.io.FileNotFoundException if the source file does not exist
	 * @throws IOException if source and destination are the same file or copying fails
	 */
	public static void copyFile(File srcFile, File destFile) throws IOException {
		canRead(srcFile);
		Assert.isNull(destFile, "Destination file must not be null.");
		if (srcFile.getCanonicalPath().equals(destFile.getCanonicalPath())) {
			throw new IOException("Source file '" + srcFile + "' and destination file '" + destFile + "' are the same.");
		}
		canWrite(destFile);
		doCopyFile(srcFile, destFile);
	}

	private static void doCopyFile(File source, File destination) throws IOException {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		FileChannel in = null;
		FileChannel out = null;
		try {
			fis = new FileInputStream(source);
			in = fis.getChannel();
			fos = new FileOutputStream(destination);
			out = fos.getChannel();
			long size = in.size();
			long pos = 0;
			long count = 0;
			long transferred;
			while (pos < size) {
				count = size - pos > FILE_COPY_BUFFER_SIZE ? FILE_COPY_BUFFER_SIZE : size - pos;
				transferred = out.transferFrom(in, pos, count);
				if (transferred <= 0) {
					break;
				}
				pos += transferred;
			}
		} finally {
			closeQuietly(out);
			closeQuietly(in);
			closeQuietly(fos);
			closeQuietly(fis);
		}
		if (source.length() != destination.length()) {
			throw new IOException("Failed to copy full contents from '" + source + "' to '" + destination + "'.");
		}
		if (!destination.setLastModified(System.currentTimeMillis())) {
			throw new IOException("Unable to set the last modification time for " + destination);
		}
	}

	/**
	 * Copies the files into the directory, keeping their names.
	 */
	public static void copyFilesToDirectory(File[] files, File destDir) throws IOException {
		for (File file : files) {
			copyFileToDirectory(file, destDir);
		}
	}

	/**
	 * Copies the whole directory tree, including empty sub directories.
	 */
	public static void copyDirectory(File srcDir, File destDir) throws IOException {
		copyDirectory(srcDir, destDir, null);
	}

	/**
	 * Copies the directory tree; the filter (if not {@code null}) selects files and sub directories to copy. Copying into a
	 * sub directory of the source is supported, the destination itself is not copied again.
	 *
	 * @throws IOException if source and destination are the same directory or copying fails
	 */
	public static void copyDirectory(File srcDir, File destDir, FileFilter filter) throws IOException {
		canScan(srcDir);
		Assert.isNull(destDir, "Destination directory must not be null.");
		if (srcDir.getCanonicalPath().equals(destDir.getCanonicalPath())) {
			throw new IOException("Source directory '" + srcDir + "' and destination directory '" + destDir + "' are the same.");
		}
		canChange(destDir);
		List<String> exclusionList = null;
		if (isSameOrSubPath(srcDir, destDir)) {
			File[] srcFiles = filter == null ? srcDir.listFiles() : srcDir.listFiles(filter);
			if (srcFiles != null) {
				exclusionList = new ArrayList<String>(srcFiles.length + 1);
				exclusionList.add(destDir.getCanonicalPath());
				for (File srcFile : srcFiles) {
					File copiedFile = new File(destDir, srcFile.getName());
					exclusionList.add(copiedFile.getCanonicalPath());
				}
			}
		}
		doCopyDirectory(srcDir, destDir, filter, exclusionList);
	}

	private static void doCopyDirectory(File srcDir, File destDir, FileFilter filter, List<String> exclusionList) throws IOException {
		File[] srcFiles = filter == null ? srcDir.listFiles() : srcDir.listFiles(filter);
		if (srcFiles == null) {
			return;
		}
		for (File srcFile : srcFiles) {
			File destFile = new File(destDir, srcFile.getName());
			if (exclusionList == null || !exclusionList.contains(srcFile.getCanonicalPath())) {
				if (srcFile.isDirectory()) {
					mkdirs(destFile);
					doCopyDirectory(srcFile, destFile, filter, exclusionList);
				} else {
					doCopyFile(srcFile, destFile);
				}
			}
		}
		if (!destDir.setLastModified(System.currentTimeMillis())) {
			throw new IOException("Unable to set the last modification time for " + destDir);
		}
	}

	/**
	 * Copies the file to the output stream, which is not closed.
	 *
	 * @return the number of bytes copied
	 */
	public static long copyFile(File input, OutputStream output) throws IOException {
		try (FileInputStream fis = openInputStream(input)) {
			return IOUtils.copy(fis, output);
		}
	}

	public static void copyFile(File file, Writer writer) throws IOException {
		copyFile(file, writer, null);
	}

	/**
	 * Copies the file decoded with the charset (the platform default if {@code null}) to the writer, which is not closed.
	 */
	public static void copyFile(File file, Writer writer, String charset) throws IOException {
		FileInputStream fis = openInputStream(file);
		try {
			IOUtils.copy(fis, writer, CharsetUtils.toCharset(charset));
		} finally {
			closeQuietly(fis);
		}
	}

	public static void copyFile(File file, List<String> collection) throws IOException {
		copyFile(file, collection, null);
	}

	/**
	 * Adds every line of the file decoded with the charset to the list.
	 */
	public static void copyFile(File file, List<String> collection, Charset charset) throws IOException {
		FileInputStream in = openInputStream(file);
		try {
			IOUtils.copyLines(in, charset, collection);
		} finally {
			closeQuietly(in);
		}
	}

	/**
	 * Appends every line of the file followed by {@link IOUtils#NEWLINE} to the builder.
	 *
	 * @return the number of lines plus one, see {@link IOUtils#copyLines(Reader, StringBuilder)}
	 */
	public static int copyFile(File file, StringBuilder str, Charset charset) throws IOException {
		FileInputStream in = openInputStream(file);
		try {
			return IOUtils.copyLines(in, charset, str);
		} finally {
			closeQuietly(in);
		}
	}

	/**
	 * Appends every line of the file followed by {@link IOUtils#NEWLINE} to the buffer.
	 *
	 * @return the number of lines plus one, see {@link IOUtils#copyLines(Reader, StringBuffer)}
	 */
	public static int copyFile(File file, StringBuffer str, Charset charset) throws IOException {
		FileInputStream in = openInputStream(file);
		try {
			return IOUtils.copyLines(in, charset, str);
		} finally {
			closeQuietly(in);
		}
	}

	/**
	 * Truncates the file to zero length (creating it if necessary).
	 */
	public static void clearFile(File file, Charset charset) throws IOException {
		writeFile("", file, false, charset);
	}

	/**
	 * Returns the absolute paths of the entries of the directory, directories first.
	 */
	public static List<String> list(File directory, FileFilter filter) throws IOException {
		List<File> files = listFiles(directory, filter);
		return files.stream().map(file -> file.getAbsolutePath()).collect(Collectors.toList());
	}

	/**
	 * Returns the entries of the directory accepted by the filter (all if {@code null}), directories first.
	 *
	 * @throws IOException if the argument is not an existing directory
	 */
	public static List<File> listFiles(File directory, FileFilter filter) throws IOException {
		FileAssert.isNotDirectory(directory);
		File[] fileArray = filter != null ? directory.listFiles(filter) : directory.listFiles();
		List<File> directories = new ArrayList<File>();
		if (ArrayUtils.isNotEmpty(fileArray)) {
			List<File> files = new ArrayList<File>();
			for (File file : fileArray) {
				if (file.isDirectory()) {
					directories.add(file);
				} else if (file.isFile()) {
					files.add(file);
				}
			}
			directories.addAll(files);
		}
		return directories;
	}

	/**
	 * Returns the total length of all files in the directory tree accepted by the filter (all files if {@code null}). The
	 * filter is applied to files only, every sub directory is scanned. Symbolic links are skipped.
	 */
	public static long sizeOfDirectory(File directory, FileFilter filter) throws IOException {
		final AtomicLong total = new AtomicLong();
		scan(directory, null, (dir, depth, file) -> {
			if (filter == null || filter.accept(file)) {
				total.addAndGet(file.length());
			}
		});
		return total.get();
	}

	/**
	 * Scans the directory tree depth first, skipping symbolic links. The filter (if not {@code null}) is applied to files and
	 * directories; a directory is only entered if {@link ScanFilter#filterDirectory(File, int)} returns {@code true}. The
	 * direct children of {@code directory} have depth 0.
	 *
	 * @throws IOException if the argument is not an existing directory
	 */
	public static void scan(File directory, FileFilter filter, ScanFilter scanFilter) throws IOException {
		scan(directory, filter, 0, scanFilter);
	}

	private static void scan(File directory, FileFilter filter, int depth, ScanFilter scanFilter) throws IOException {
		FileAssert.isNotDirectory(directory);
		File[] files = filter != null ? directory.listFiles(filter) : directory.listFiles();
		if (ArrayUtils.isNotEmpty(files)) {
			files = FileSorter.sort(files);
			for (File file : files) {
				if (!isSymlink(file)) {
					if (file.isDirectory()) {
						if (scanFilter.filterDirectory(file, depth)) {
							scan(file, filter, depth + 1, scanFilter);
						}
					} else {
						scanFilter.filterFile(directory, depth, file);
					}
				}
			}
		}
	}

	/**
	 * Inserts the text encoded with the charset at the byte position, shifting the following content.
	 *
	 * @throws java.io.FileNotFoundException if the file does not exist
	 * @throws IllegalArgumentException if the content is {@code null}
	 */
	public static void insertContent(File file, int position, CharSequence content, Charset charset) throws IOException {
		canRead(file);
		Assert.isNull(content, "Content must not be null.");
		try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
			raf.seek(position);
			byte[] store = IOUtils.toByteArray(raf);
			raf.write(content.toString().getBytes(CharsetUtils.toCharset(charset)));
			raf.write(store);
		}
	}

}
