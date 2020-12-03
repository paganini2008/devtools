package com.github.paganini2008.devtools.io;

import static com.github.paganini2008.devtools.Assert.isFalse;
import static com.github.paganini2008.devtools.Assert.isTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * FileAssert
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class FileAssert {

	private FileAssert() {
	}

	public static void isFile(File arg) throws IOException {
		isFile(arg, "File '" + arg + "' is existed but is a file.");
	}

	public static void isFile(File arg, String msg) throws IOException {
		isTrue(arg != null && arg.exists() && arg.isFile(), new IOException(msg));
	}

	public static void isDirectory(File arg) throws IOException {
		isDirectory(arg, "File '" + arg + "' is existed but is a directory.");
	}

	public static void isDirectory(File arg, String msg) throws IOException {
		isTrue(arg != null && arg.exists() && arg.isDirectory(), new IOException(msg));
	}

	public static void existed(File arg) throws IOException {
		existed(arg, "File '" + arg + "' is already existed.");
	}

	public static void existed(File arg, String msg) throws IOException {
		isTrue(arg != null && arg.exists(), new IOException(msg));
	}

	public static void notExisted(File arg) throws FileNotFoundException {
		notExisted(arg, "File '" + arg + "' is not existed.");
	}

	public static void notExisted(File arg, String msg) throws FileNotFoundException {
		isFalse(arg != null && arg.exists(), new FileNotFoundException(msg));
	}

	public static void cannotWrite(File arg) throws IOException {
		cannotWrite(arg, "File '" + arg + "' can not write.");
	}

	public static void cannotWrite(File arg, String msg) throws IOException {
		isFalse(arg != null && arg.exists() && arg.canWrite(), new IOException(msg));
	}

	public static void cannotRead(File arg) throws IOException {
		cannotRead(arg, "File '" + arg + "' can not read.");
	}

	public static void cannotRead(File arg, String msg) throws IOException {
		isFalse(arg != null && arg.exists() && arg.canRead(), new IOException(msg));
	}

}
