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

import java.io.File;
import java.net.URL;

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.StringUtils;

/**
 * 
 * Path string utilities that understand both Unix ({@code /}) and Windows ({@code \\}) separators.
 *
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public abstract class PathUtils {

	public static final char EXTENSION_SEPARATOR_CHAR = '.';

	public static final String EXTENSION_SEPARATOR = Character.toString(EXTENSION_SEPARATOR_CHAR);

	public static final char UNIX_SEPARATOR_CHAR = '/';

	public static final String UNIX_SEPARATOR = Character.toString(UNIX_SEPARATOR_CHAR);

	public static final char WINDOWS_SEPARATOR_CHAR = '\\';

	public static final String WINDOWS_SEPARATOR = Character.toString(WINDOWS_SEPARATOR_CHAR);

	public static final char SYSTEM_SEPARATOR_CHAR = File.separatorChar;

	public static final String SYSTEM_SEPARATOR = Character.toString(SYSTEM_SEPARATOR_CHAR);

	/**
	 * Tests whether the system file separator is {@code '/'}.
	 */
	public static boolean isUnixOS() {
		return SYSTEM_SEPARATOR_CHAR == UNIX_SEPARATOR_CHAR;
	}

	/**
	 * Tests whether the system file separator is {@code '\\'}.
	 */
	public static boolean isWindowsOS() {
		return SYSTEM_SEPARATOR_CHAR == WINDOWS_SEPARATOR_CHAR;
	}

	/**
	 * Returns the root of the class path of this class (e.g. the {@code classes} directory) without the {@code file:}
	 * prefix, or {@code null} if it cannot be determined.
	 */
	public static String runRootPath() {
		URL url = PathUtils.class.getClassLoader().getResource("");
		if (url == null) {
			return null;
		}
		String cp = url.toExternalForm();
		if (cp.startsWith("file:")) {
			cp = cp.substring("file:".length());
		}
		return cp;
	}

	/**
	 * Returns the file name without directory and extension.
	 *
	 * <pre>
	 * PathUtils.getBaseName("a/b/c.txt") = "c"
	 * PathUtils.getBaseName("a.b/c")     = "c"
	 * PathUtils.getBaseName(".bashrc")   = ""
	 * PathUtils.getBaseName(null)        = ""
	 * </pre>
	 */
	public static String getBaseName(String path) {
		if (StringUtils.isBlank(path)) {
			return "";
		}
		int i = indexOfLastSeparator(path);
		int j = indexOfExtension(path);
		return j > i ? path.substring(i + 1, j) : path.substring(i + 1);
	}

	/**
	 * Returns the last segment of the path.
	 *
	 * <pre>
	 * PathUtils.getName("a/b/c.txt") = "c.txt"
	 * PathUtils.getName("c.txt")     = "c.txt"
	 * PathUtils.getName("a/b/")      = ""
	 * PathUtils.getName(null)        = ""
	 * </pre>
	 */
	public static String getName(String path) {
		if (StringUtils.isBlank(path)) {
			return "";
		}
		int index = indexOfLastSeparator(path);
		return path.substring(index + 1);
	}

	/**
	 * Returns the text after the last dot of the file name. A dot at the very beginning of the path does not start an
	 * extension.
	 *
	 * <pre>
	 * PathUtils.getExtension("a/b.txt")      = "txt"
	 * PathUtils.getExtension("a.b/c")        = ""
	 * PathUtils.getExtension(".bashrc")      = ""
	 * PathUtils.getExtension("home/.bashrc") = "bashrc"
	 * PathUtils.getExtension(null)           = ""
	 * </pre>
	 */
	public static String getExtension(String path) {
		if (StringUtils.isBlank(path)) {
			return "";
		}
		int index = indexOfExtension(path);
		return index > 0 ? path.substring(index + 1) : "";
	}

	/**
	 * Returns the absolute working directory with normalized separators and without a trailing separator.
	 */
	public static String getProjectPath() {
		return formatKeepingRoot(new File("").getAbsolutePath());
	}

	/**
	 * Replaces every sequence of {@code '/'} and {@code '\\'} with the system separator and removes a leading and a trailing
	 * separator.
	 *
	 * <pre>
	 * PathUtils.format("//a\\b//c/") = "a/b/c"   (on Unix)
	 * PathUtils.format("/")          = ""
	 * PathUtils.format(null)         = ""
	 * </pre>
	 */
	public static String format(String path) {
		if (StringUtils.isBlank(path)) {
			return "";
		}
		path = path.replaceAll("[\\\\/]+", isWindowsOS() ? "\\\\" : "/");
		if (path.length() > 0 && path.charAt(0) == SYSTEM_SEPARATOR_CHAR) {
			path = path.substring(1);
		}
		if (path.length() > 0 && path.charAt(path.length() - 1) == SYSTEM_SEPARATOR_CHAR) {
			path = path.substring(0, path.length() - 1);
		}
		return path;
	}

	private static String formatKeepingRoot(String path) {
		String result = format(path);
		char first = path.charAt(0);
		return first == UNIX_SEPARATOR_CHAR || first == WINDOWS_SEPARATOR_CHAR ? SYSTEM_SEPARATOR + result : result;
	}

	/**
	 * Returns the index of the last {@code '/'} or {@code '\\'}, or -1.
	 */
	public static int indexOfLastSeparator(String path) {
		if (StringUtils.isBlank(path)) {
			return -1;
		}
		int lastUnixPos = path.lastIndexOf(UNIX_SEPARATOR);
		int lastWindowsPos = path.lastIndexOf(WINDOWS_SEPARATOR);
		return Math.max(lastUnixPos, lastWindowsPos);
	}

	/**
	 * Returns the index of the dot starting the extension of the file name, or -1.
	 *
	 * <pre>
	 * PathUtils.indexOfExtension("a/b.c") = 3
	 * PathUtils.indexOfExtension("a.b/c") = -1
	 * </pre>
	 */
	public static int indexOfExtension(String path) {
		if (StringUtils.isBlank(path)) {
			return -1;
		}
		int extensionPos = path.lastIndexOf(EXTENSION_SEPARATOR_CHAR);
		int lastSeparator = indexOfLastSeparator(path);
		return lastSeparator > extensionPos ? -1 : extensionPos;
	}

	/**
	 * Returns the index of the first {@code '/'} or {@code '\\'}, or -1.
	 *
	 * <pre>
	 * PathUtils.indexOfSeparator("a\\b/c") = 1
	 * PathUtils.indexOfSeparator("abc")    = -1
	 * </pre>
	 */
	public static int indexOfSeparator(String path) {
		if (StringUtils.isBlank(path)) {
			return -1;
		}
		int unixPos = path.indexOf(UNIX_SEPARATOR_CHAR);
		int windowsPos = path.indexOf(WINDOWS_SEPARATOR_CHAR);
		if (unixPos < 0) {
			return windowsPos;
		}
		return windowsPos < 0 ? unixPos : Math.min(unixPos, windowsPos);
	}

	/**
	 * Returns the directory part of the path with normalized separators, or {@code ""} if there is none. A leading
	 * separator of an absolute path is kept.
	 *
	 * <pre>
	 * PathUtils.getParent("/data/report.final.pdf") = "/data"   (on Unix)
	 * PathUtils.getParent("/c")        = "/"         (on Unix)
	 * PathUtils.getParent("a/b/c.txt") = "a/b"
	 * PathUtils.getParent("a.b/c")     = "a.b"
	 * PathUtils.getParent("c.txt")     = ""
	 * </pre>
	 *
	 * @throws IllegalArgumentException if the path is empty
	 */
	public static String getParent(String file) {
		Assert.hasNoText(file, "Unspecified file path.");
		int n = indexOfLastSeparator(file);
		if (n == 0) {
			return SYSTEM_SEPARATOR;
		}
		return n > 0 ? formatKeepingRoot(file.substring(0, n)) : "";
	}
}
