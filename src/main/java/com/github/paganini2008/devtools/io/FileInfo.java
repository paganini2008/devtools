package com.github.paganini2008.devtools.io;

import java.io.File;

/**
 * 
 * FileInfo
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @created 2013-06
 * @version 1.0
 */
public interface FileInfo {

	File getFile();
	
	int getFileCount();

	int getFolderCount();

	long getLength();
	
	long getStartTime();

	long getElapsed();
}
