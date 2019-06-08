package com.github.paganini2008.devtools.io;

import java.io.File;

/**
 * 
 * DirectoryInfo
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @version 1.0
 */
public interface DirectoryInfo {

	File getFile();

	int getFileCount();

	int getFolderCount();

	long getLength();
}
