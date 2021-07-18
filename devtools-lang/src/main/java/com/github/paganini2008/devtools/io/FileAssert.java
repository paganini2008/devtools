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

import static com.github.paganini2008.devtools.Assert.isFalse;
import static com.github.paganini2008.devtools.Assert.isTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * FileAssert
 * 
 * @author Fred Feng
 * @version 1.0
 */
public abstract class FileAssert {

	public static void isFile(File arg) throws IOException {
		isFile(arg, "File '" + arg + "' is existed but is a file.");
	}

	public static void isFile(File file, String msg) throws IOException {
		isTrue(file != null && file.exists() && file.isFile(), new IOException(msg));
	}

	public static void isNotFile(File file) throws IOException {
		isNotFile(file, "File '" + file + "' dosen't exists or it's not a file.");
	}

	public static void isNotFile(File file, String msg) throws IOException {
		isFalse(file != null && file.exists() && file.isFile(), new IOException(msg));
	}

	public static void isDirectory(File file) throws IOException {
		isDirectory(file, "File '" + file + "' is existed but is a directory.");
	}

	public static void isDirectory(File file, String msg) throws IOException {
		isTrue(file != null && file.exists() && file.isDirectory(), new IOException(msg));
	}

	public static void isNotDirectory(File file) throws IOException {
		isNotDirectory(file, "File '" + file + "' doesn't exists or it's not a directory.");
	}

	public static void isNotDirectory(File file, String msg) throws IOException {
		isFalse(file != null && file.exists() && file.isDirectory(), new IOException(msg));
	}

	public static void existed(File file) throws IOException {
		existed(file, "File '" + file + "' is already existed.");
	}

	public static void existed(File file, String msg) throws IOException {
		isTrue(file != null && file.exists(), new IOException(msg));
	}

	public static void notExisted(File file) throws FileNotFoundException {
		notExisted(file, "File '" + file + "' is not existed.");
	}

	public static void notExisted(File file, String msg) throws FileNotFoundException {
		isFalse(file != null && file.exists(), new FileNotFoundException(msg));
	}

	public static void cannotWrite(File file) throws IOException {
		cannotWrite(file, "File '" + file + "' doesn't exists or can not be writen.");
	}

	public static void cannotWrite(File file, String msg) throws IOException {
		isFalse(file != null && file.exists() && file.canWrite(), new IOException(msg));
	}

	public static void cannotRead(File file) throws IOException {
		cannotRead(file, "File '" + file + "' doesn't exists or can not be read.");
	}

	public static void cannotRead(File file, String msg) throws IOException {
		isFalse(file != null && file.exists() && file.canRead(), new IOException(msg));
	}

}
