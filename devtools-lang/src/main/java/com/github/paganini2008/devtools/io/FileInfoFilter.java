package com.github.paganini2008.devtools.io;

import java.io.File;
import java.io.FileFilter;

import com.github.paganini2008.devtools.io.DirectoryWalker.FileInfo;

/**
 * 
 * FileInfoFilter
 * 
 * @author Fred Feng
 * 
 * @version 1.0
 */
public interface FileInfoFilter extends FileFilter {

	boolean accept(FileInfo fileInfo);

	@Override
	default boolean accept(File pathname) {
		return false;
	}

}
