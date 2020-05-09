package com.github.paganini2008.devtools.io;

import java.io.File;
import java.io.IOException;

/**
 * 
 * DirectoryWalkerHandler
 *
 * @author Fred Feng
 * @since 1.0
 */
public interface DirectoryWalkerHandler {

	default void handleDirectoryStart(File file, int depth) throws IOException {
	}

	default void handleDirectoryEnd(File file, Directory directory, int depth) throws IOException {
	}

	default boolean handleDirectoryOnError(File file, int depth, Throwable e) {
		return true;
	}

	default boolean shouldHandleDirectory(File directory, int depth) throws IOException {
		return true;
	}

	default boolean shouldHandleFile(File file, int depth) throws IOException {
		return true;
	}

	void handleFile(File file, int depth) throws Exception;

	default boolean handleFileOnError(File file, int depth, Throwable e) {
		return true;
	}

}
