package com.github.paganini2008.devtools.io;

import java.io.File;
import java.io.FileFilter;

/**
 * 
 * DirectoryFilter
 * 
 * @author Fred Feng
 * 
 * @version 1.0
 */
public interface DirectoryFilter extends FileFilter {

	boolean accept(Directory directory);

	@Override
	default boolean accept(File file) {
		return false;
	}

}
