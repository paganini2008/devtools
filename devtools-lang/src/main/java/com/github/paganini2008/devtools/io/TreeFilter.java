package com.github.paganini2008.devtools.io;

import java.io.File;

/**
 * 
 * TreeFilter
 *
 * @author Fred Feng
 * @version 1.0.0
 */
public interface TreeFilter {

	default boolean filterDirectory(File directory, int depth) {
		return true;
	}

	default boolean filterFile(File directory, int depth, File file) {
		return true;
	}

	String getText(File directory, int depth);

	String getText(File directory, int depth, File file);

}
