/**
* Copyright 2018-2021 Fred Feng (paganini.fy@gmail.com)

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

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.StringUtils;

/**
 * 
 * PathUtils
 *
 * @author Fred Feng
 * @version 1.0
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

	public static boolean isUnixOS() {
		return SYSTEM_SEPARATOR_CHAR == UNIX_SEPARATOR_CHAR;
	}

	public static boolean isWindowsOS() {
		return SYSTEM_SEPARATOR_CHAR == WINDOWS_SEPARATOR_CHAR;
	}

	public static String runRootPath() {
		String cp = PathUtils.class.getClassLoader().getResource("").toExternalForm();
		if (cp.startsWith("file:")) {
			cp = cp.substring("file:".length());
		}
		return cp;
	}

	public static void main(String[] args) {
		System.out.println(runRootPath());
	}

	public static String getBaseName(String path) {
		if (StringUtils.isBlank(path)) {
			return "";
		}
		int i = indexOfLastSeparator(path);
		int j = indexOfExtension(path);
		return j > i ? path.substring(i + 1, j) : path.substring(i + 1);
	}

	public static String getName(String path) {
		if (StringUtils.isBlank(path)) {
			return "";
		}
		int index = indexOfLastSeparator(path);
		return index > 0 ? path.substring(index + 1) : "";
	}

	public static String getExtension(String path) {
		if (StringUtils.isBlank(path)) {
			return "";
		}
		int index = indexOfExtension(path);
		return index > 0 ? path.substring(index + 1) : "";
	}

	public static String getProjectPath() {
		return format(new File("").getAbsolutePath());
	}

	public static String format(String path) {
		if (StringUtils.isBlank(path)) {
			return "";
		}
		path = path.replaceAll("[\\\\|/]+", isWindowsOS() ? "\\\\" : "/");
		if (path.charAt(0) == SYSTEM_SEPARATOR_CHAR) {
			path = path.substring(1);
		}
		if (path.charAt(path.length() - 1) == SYSTEM_SEPARATOR_CHAR) {
			path = path.substring(0, path.length() - 1);
		}
		return path;
	}

	public static int indexOfLastSeparator(String path) {
		if (StringUtils.isBlank(path)) {
			return -1;
		}
		int lastUnixPos = path.lastIndexOf(UNIX_SEPARATOR);
		int lastWindowsPos = path.lastIndexOf(WINDOWS_SEPARATOR);
		return Math.max(lastUnixPos, lastWindowsPos);
	}

	public static int indexOfExtension(String path) {
		if (StringUtils.isBlank(path)) {
			return -1;
		}
		int extensionPos = path.lastIndexOf(EXTENSION_SEPARATOR_CHAR);
		int lastSeparator = indexOfLastSeparator(path);
		return lastSeparator > extensionPos ? -1 : extensionPos;
	}

	public static int indexOfSeparator(String path) {
		if (StringUtils.isBlank(path)) {
			return -1;
		}
		int lastUnixPos = path.indexOf(UNIX_SEPARATOR);
		int lastWindowsPos = path.indexOf(WINDOWS_SEPARATOR);
		return Math.max(lastUnixPos, lastWindowsPos);
	}

	public static String getParent(String file) {
		Assert.hasNoText(file, "Unspecified file path.");
		int i = file.lastIndexOf(".");
		int j = indexOfLastSeparator(file);
		int n = i > 0 ? Math.min(i, j) : Math.max(i, j);
		return n > 0 ? format(file.substring(0, n)) : "";
	}

}
