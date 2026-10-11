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
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Assertions on files throwing {@link IOException}s. Note that the method names describe the <em>failing</em> condition,
 * e.g. {@link #isFile(File)} throws if the argument is an existing file.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public abstract class FileAssert {

	/**
	 * Throws if the argument is an existing file.
	 */
	public static void isFile(File arg) throws IOException {
		isFile(arg, "File '" + arg + "' is existed but is a file.");
	}

	public static void isFile(File file, String msg) throws IOException {
		if (file != null && file.exists() && file.isFile()) {
			throw new IOException(msg);
		}
	}

	/**
	 * Throws if the argument is not an existing file (including {@code null}).
	 */
	public static void isNotFile(File file) throws IOException {
		isNotFile(file, "File '" + file + "' dosen't exists or it's not a file.");
	}

	public static void isNotFile(File file, String msg) throws IOException {
		if (!(file != null && file.exists() && file.isFile())) {
			throw new IOException(msg);
		}
	}

	/**
	 * Throws if the argument is an existing directory.
	 */
	public static void isDirectory(File file) throws IOException {
		isDirectory(file, "File '" + file + "' is existed but is a directory.");
	}

	public static void isDirectory(File file, String msg) throws IOException {
		if (file != null && file.exists() && file.isDirectory()) {
			throw new IOException(msg);
		}
	}

	/**
	 * Throws if the argument is not an existing directory (including {@code null}).
	 */
	public static void isNotDirectory(File file) throws IOException {
		isNotDirectory(file, "File '" + file + "' doesn't exists or it's not a directory.");
	}

	public static void isNotDirectory(File file, String msg) throws IOException {
		if (!(file != null && file.exists() && file.isDirectory())) {
			throw new IOException(msg);
		}
	}

	/**
	 * Throws if the file exists.
	 */
	public static void existed(File file) throws IOException {
		existed(file, "File '" + file + "' is already existed.");
	}

	public static void existed(File file, String msg) throws IOException {
		if (file != null && file.exists()) {
			throw new IOException(msg);
		}
	}

	/**
	 * Throws a {@link FileNotFoundException} if the file does not exist (including {@code null}).
	 */
	public static void notExisted(File file) throws FileNotFoundException {
		notExisted(file, "File '" + file + "' is not existed.");
	}

	public static void notExisted(File file, String msg) throws FileNotFoundException {
		if (!(file != null && file.exists())) {
			throw new FileNotFoundException(msg);
		}
	}

	/**
	 * Throws if the file does not exist or is not writable.
	 */
	public static void cannotWrite(File file) throws IOException {
		cannotWrite(file, "File '" + file + "' doesn't exists or can not be writen.");
	}

	public static void cannotWrite(File file, String msg) throws IOException {
		if (!(file != null && file.exists() && file.canWrite())) {
			throw new IOException(msg);
		}
	}

	/**
	 * Throws if the file does not exist or is not readable.
	 */
	public static void cannotRead(File file) throws IOException {
		cannotRead(file, "File '" + file + "' doesn't exists or can not be read.");
	}

	public static void cannotRead(File file, String msg) throws IOException {
		if (!(file != null && file.exists() && file.canRead())) {
			throw new IOException(msg);
		}
	}

}
