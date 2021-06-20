/**
* Copyright 2021 Fred Feng (paganini.fy@gmail.com)

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
import java.io.ByteArrayInputStream;
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
import com.github.paganini2008.devtools.math.BigDecimalUtils;

/**
 * FileUtils
 * 
 * @author Fred Feng
 * @version 1.0
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

	public static File getFile(File directory, String... names) {
		Assert.isNull(directory, "Destination directory must not be null.");
		Assert.isNull(names, "Names must not be null.");
		File file = directory;
		for (String name : names) {
			file = new File(file, name);
		}
		return file;
	}

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

	public static String getBaseName(File file) {
		return file != null ? PathUtils.getBaseName(file.getName()) : "";
	}

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

	public static RandomAccessFile getRandomAccessFile(File file) throws IOException {
		canRead(file);
		return new RandomAccessFile(file, "rw");
	}

	public static FileInputStream openInputStream(String file) throws IOException {
		return openInputStream(new File(file));
	}

	public static FileInputStream openInputStream(File file) throws IOException {
		canRead(file);
		return new FileInputStream(file);
	}

	public static BufferedReader getBufferedReader(String filePath, String charset) throws IOException {
		return getBufferedReader(new File(filePath), charset);
	}

	public static BufferedReader getBufferedReader(File file, String charset) throws IOException {
		return IOUtils.getBufferedReader(openInputStream(file), CharsetUtils.toCharset(charset));
	}

	public static BufferedReader getBufferedReader(File file, String charset, int bufferSize) throws IOException {
		return IOUtils.getBufferedReader(openInputStream(file), CharsetUtils.toCharset(charset), bufferSize);
	}

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

	public static FileOutputStream openOutputStream(File file, boolean append) throws IOException {
		canWrite(file);
		return new FileOutputStream(file, append);
	}

	private static void writeBOM(FileOutputStream fos, Charset charset) throws IOException {
		if (CharsetUtils.UTF_8.equals(charset)) {
			fos.write(CharsetUtils.BOM_UTF_8);
		} else if (CharsetUtils.UTF_16BE.equals(charset)) {
			fos.write(CharsetUtils.BOM_UTF_16BE);
		} else if (CharsetUtils.UTF_16LE.equals(charset)) {
			fos.write(CharsetUtils.BOM_UTF_16LE);
		}
	}

	public static BufferedWriter getBufferedWriter(String filePath, Charset charset) throws IOException {
		return getBufferedWriter(new File(filePath), charset);
	}

	public static BufferedWriter getBufferedWriter(File file, Charset charset) throws IOException {
		return getBufferedWriter(file, false, charset);
	}

	public static BufferedWriter getBufferedWriter(File file, boolean append, Charset charset) throws IOException {
		FileOutputStream fos = openOutputStream(file, append);
		writeBOM(fos, charset);
		return IOUtils.getBufferedWriter(fos, charset);
	}

	public static String formatSize(long size) {
		return formatSize(size, 2);
	}

	public static String formatSize(long size, int scale) {
		return formatSize(BigInteger.valueOf(size), scale);
	}

	public static String formatSize(BigInteger size) {
		return formatSize(size, 2);
	}

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

	public static boolean isFileNewer(File file, File reference) throws IOException {
		FileAssert.notExisted(reference);
		return isFileNewer(file, reference.lastModified());
	}

	public static boolean isFileNewer(File file, Date referenceDate) throws IOException {
		Assert.isNull(referenceDate, "Unspecified referenceDate.");
		return isFileNewer(file, referenceDate.getTime());
	}

	public static boolean isFileNewer(File file, long timeMillis) throws IOException {
		FileAssert.notExisted(file);
		return file.lastModified() > timeMillis;
	}

	public static boolean isFileOlder(File file, File reference) throws IOException {
		FileAssert.notExisted(reference);
		return isFileOlder(file, reference.lastModified());
	}

	public static boolean isFileOlder(File file, Date referenceDate) throws IOException {
		Assert.isNull(referenceDate, "Unspecified referenceDate.");
		return isFileOlder(file, referenceDate.getTime());
	}

	public static boolean isFileOlder(File file, long timeMillis) throws IOException {
		FileAssert.notExisted(file);
		return file.lastModified() < timeMillis;
	}

	public static boolean isSymlink(File file) throws IOException {
		Assert.isNull(file, "File must not be null.");
		if (PathUtils.isWindowsOS()) {
			return false;
		}
		File fileInCanonicalDir = null;
		if (file.getParent() == null) {
			fileInCanonicalDir = file;
		} else {
			File canonicalDir = file.getParentFile().getCanonicalFile();
			fileInCanonicalDir = new File(canonicalDir, file.getName());
		}
		if (fileInCanonicalDir.getCanonicalFile().equals(fileInCanonicalDir.getAbsoluteFile())) {
			return false;
		} else {
			return true;
		}
	}

	public static void deleteDirectory(File directory) throws IOException {
		canChange(directory);
		if (!isSymlink(directory)) {
			cleanDirectory(directory);
		}
		if (!directory.delete()) {
			throw new IOException("Unable to delete directory " + directory);
		}
	}

	public static void cleanDirectory(File directory) throws IOException {
		canChange(directory);
		File[] files = directory.listFiles();
		if (files != null) {
			for (File file : files) {
				deleteFile(file);
			}
		}
	}

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

	public static String readFileToString(File file, Charset charset) throws IOException {
		Assert.isNull(file, "File must not be null.");
		StringBuilder str = new StringBuilder();
		copyFile(file, str, charset);
		return str.toString();
	}

	public static String readFileToString(String filePath, Charset charset) throws IOException {
		return readFileToString(new File(filePath), charset);
	}

	public static boolean exists(File file) {
		return file != null ? file.exists() : false;
	}

	public static boolean notExists(File file) {
		return !exists(file);
	}

	public static boolean notExists(String file) {
		return !exists(file);
	}

	public static boolean exists(String file) {
		return file != null ? new File(file).exists() : false;
	}

	public static boolean isAvailable(String file) {
		return file != null ? isAvailable(new File(file)) : false;
	}

	public static boolean isAvailable(File f) {
		return f != null ? (f.exists() && f.canRead() && f.canWrite()) : false;
	}

	public static boolean isNotAvailable(String file) {
		return !isAvailable(file);
	}

	public static boolean isNotAvailable(File f) {
		return !isAvailable(f);
	}

	public static List<String> readLines(File file) throws IOException {
		return readLines(file, CharsetUtils.UTF_8);
	}

	public static List<String> readLines(File file, Charset charset) throws IOException {
		Assert.isNull(file, "File must not be null.");
		List<String> list = new ArrayList<String>();
		copyFile(file, list, charset);
		return list;
	}

	public static int getLineNumber(File file, Charset charset) throws IOException {
		LineNumberReader reader = null;
		try {
			reader = getLineNumberReader(file, charset);
			reader.skip(file.length());
			return reader.getLineNumber();
		} finally {
			closeQuietly(reader);
		}
	}

	public static void split(File file, int line, File output, String template) throws IOException {
		split(file, null, line, output, template);
	}

	public static void split(File file, Charset charset, int line, File output, String template) throws IOException {
		int rows = getLineNumber(file, charset);
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

	public static byte[] readFileToByteArray(File file) throws IOException {
		FileInputStream in = openInputStream(file);
		try {
			return IOUtils.toByteArray(in);
		} finally {
			closeQuietly(in);
		}
	}

	public static char[] readFileToCharArray(File file, Charset charset) throws IOException {
		Assert.isNull(file, "File must not be null.");
		InputStream in = null;
		try {
			in = openInputStream(file);
			return IOUtils.toCharArray(in, charset);
		} finally {
			closeQuietly(in);
		}
	}

	public static void writeFile(byte[] bytes, File file, boolean append) throws IOException {
		Assert.isNull(bytes, "Bytes must not be null.");
		Assert.isNull(file, "Destination file must not be null.");
		InputStream in = null;
		try {
			in = new ByteArrayInputStream(bytes);
			writeFile(in, file, append);
		} finally {
			closeQuietly(in);
		}
	}

	public static void writeFile(InputStream in, File file, boolean append) throws IOException {
		Assert.isNull(in, "InputStream must not be null.");
		Assert.isNull(file, "Destination file must not be null.");
		OutputStream os = null;
		try {
			os = openOutputStream(file, append);
			IOUtils.copy(in, os);
		} finally {
			closeQuietly(os);
		}
	}

	public static void writeFile(Reader reader, File file, boolean append, Charset charset) throws IOException {
		Assert.isNull(reader, "Content must not be null.");
		Assert.isNull(file, "Destination file must not be null.");
		FileOutputStream fos = openOutputStream(file, append);
		try {
			writeBOM(fos, charset);
			IOUtils.copy(reader, fos, charset);
		} finally {
			closeQuietly(fos);
		}
	}

	public static void writeFile(char[] content, File file, boolean append, Charset charset) throws IOException {
		Assert.isNull(content, "Content must not be null.");
		Assert.isNull(file, "Destination file must not be null.");
		FileOutputStream fos = openOutputStream(file, append);
		try {
			writeBOM(fos, charset);
			IOUtils.copy(content, fos, charset);
		} finally {
			closeQuietly(fos);
		}
	}

	public static void writeFile(CharSequence content, File file, boolean append, Charset charset) throws IOException {
		Assert.isNull(content, "Content must not be null.");
		Assert.isNull(file, "Destination file must not be null.");
		FileOutputStream fos = openOutputStream(file, append);
		try {
			writeBOM(fos, charset);
			IOUtils.copy(content, fos, charset);
		} finally {
			closeQuietly(fos);
		}
	}

	public static void writeFile(Collection<String> collection, File file, boolean append, Charset charset) throws IOException {
		Assert.isNull(collection, "Content must not be null.");
		Assert.isNull(file, "Destination file must not be null.");
		FileOutputStream fos = openOutputStream(file, append);
		try {
			writeBOM(fos, charset);
			IOUtils.writeLines(collection, fos, charset);
		} finally {
			closeQuietly(fos);
		}
	}

	public static void moveFileToDirectory(File srcFile, File destDir) throws IOException {
		Assert.isNull(srcFile, "Unspecified source file.");
		Assert.isNull(destDir, "Unspecified destination directory.");
		moveFile(srcFile, new File(destDir, srcFile.getName()));
	}

	public static void moveToDirectory(File src, File destDir, boolean createDestDir) throws IOException {
		Assert.isNull(src, "Unspecified source file.");
		Assert.isNull(destDir, "Unspecified destination directory.");
		if (src.isDirectory()) {
			moveDirectoryToDirectory(src, destDir);
		} else {
			moveFileToDirectory(src, destDir);
		}
	}

	public static void moveDirectory(File srcDir, File destDir) throws IOException {
		canScan(srcDir);
		canChange(destDir);
		if (destDir.getCanonicalPath().startsWith(srcDir.getCanonicalPath())) {
			throw new IOException("Cannot move directory: " + srcDir + " to a subdirectory of itself: " + destDir);
		}
		if (!srcDir.renameTo(destDir)) {
			copyDirectory(srcDir, destDir);
			deleteDirectory(srcDir);
			if (srcDir.exists()) {
				throw new IOException("Failed to delete original directory '" + srcDir + "' after copy to '" + destDir + "'");
			}
		}
	}

	public static void moveDirectoryToDirectory(File srcDir, File destDir) throws IOException {
		Assert.isNull(srcDir, "Unspecified source directory");
		Assert.isNull(destDir, "Unspecified destination directory");
		moveDirectory(srcDir, new File(destDir, srcDir.getName()));
	}

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

	public static void copyFileToDirectory(File srcFile, File destDir) throws IOException {
		Assert.isNull(srcFile, "Unspecified source file.");
		canChange(destDir);
		File destFile = new File(destDir, srcFile.getName());
		doCopyFile(srcFile, destFile);
	}

	public static void copyFile(File srcFile, File destFile) throws IOException {
		canRead(srcFile);
		canWrite(destFile);
		if (srcFile.getCanonicalPath().equals(destFile.getCanonicalPath())) {
			throw new IOException("Source file '" + srcFile + "' and destination file '" + destFile + "' are the same.");
		}
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
			while (pos < size) {
				count = size - pos > FILE_COPY_BUFFER_SIZE ? FILE_COPY_BUFFER_SIZE : size - pos;
				pos += out.transferFrom(in, pos, count);
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

	public static void copyFilesToDirectory(File[] files, File destDir) throws IOException {
		for (File file : files) {
			copyFileToDirectory(file, destDir);
		}
	}

	public static void copyDirectory(File srcDir, File destDir) throws IOException {
		copyDirectory(srcDir, destDir, null);
	}

	public static void copyDirectory(File srcDir, File destDir, FileFilter filter) throws IOException {
		canScan(srcDir);
		canChange(destDir);
		if (srcDir.getCanonicalPath().equals(destDir.getCanonicalPath())) {
			throw new IOException("Source directory '" + srcDir + "' and destination directory '" + destDir + "' are the same.");
		}
		List<String> exclusionList = null;
		if (destDir.getCanonicalPath().startsWith(srcDir.getCanonicalPath())) {
			File[] srcFiles = filter == null ? srcDir.listFiles() : srcDir.listFiles(filter);
			if (srcFiles != null) {
				exclusionList = new ArrayList<String>(srcFiles.length);
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

	public static long copyFile(File input, OutputStream output) throws IOException {
		FileInputStream fis = openInputStream(input);
		try {
			return IOUtils.copy(fis, output);
		} finally {
			closeQuietly(fis);
		}
	}

	public static void copyFile(File file, Writer writer) throws IOException {
		copyFile(file, writer, null);
	}

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

	public static void copyFile(File file, List<String> collection, Charset charset) throws IOException {
		FileInputStream in = openInputStream(file);
		try {
			IOUtils.copyLines(in, charset, collection);
		} finally {
			closeQuietly(in);
		}
	}

	public static int copyFile(File file, StringBuilder str, Charset charset) throws IOException {
		FileInputStream in = openInputStream(file);
		try {
			return IOUtils.copyLines(in, charset, str);
		} finally {
			closeQuietly(in);
		}
	}

	public static int copyFile(File file, StringBuffer str, Charset charset) throws IOException {
		FileInputStream in = openInputStream(file);
		try {
			return IOUtils.copyLines(in, charset, str);
		} finally {
			closeQuietly(in);
		}
	}

	public static void clearFile(File file, Charset charset) throws IOException {
		writeFile("", file, false, charset);
	}

	public static List<String> list(File directory, FileFilter filter) throws IOException {
		List<File> files = listFiles(directory, filter);
		return files.stream().map(file -> file.getAbsolutePath()).collect(Collectors.toList());
	}

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

	public static long sizeOfDirectory(File directory, FileFilter filter) throws IOException {
		final AtomicLong total = new AtomicLong();
		scan(directory, filter, (dir, file) -> {
			total.addAndGet(file.length());
		});
		return total.get();
	}

	public static void scan(File directory, FileFilter filter, ScanHandler handler) throws IOException {
		FileAssert.isNotDirectory(directory);
		File[] files = filter != null ? directory.listFiles(filter) : directory.listFiles();
		if (ArrayUtils.isNotEmpty(files)) {
			for (File file : files) {
				if (!isSymlink(file)) {
					if (file.isDirectory()) {
						scan(file, filter, handler);
					} else {
						handler.handleFile(directory, file);
					}
				}
			}
		}
	}

	public static void insertContent(File file, int position, CharSequence content, Charset charset) throws IOException {
		FileAssert.cannotRead(file);
		RandomAccessFile raf = new RandomAccessFile(file, "rw");
		try {
			raf.seek(position);
			byte[] store = IOUtils.toByteArray(raf);
			raf.write(content.toString().getBytes(CharsetUtils.toCharset(charset)));
			raf.write(store);
		} finally {
			IOUtils.closeQuietly(raf);
		}
	}

}
