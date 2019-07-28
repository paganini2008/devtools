package com.github.paganini2008.devtools.io;

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

	long getStartTime();

	int getFileCount();

	int getFolderCount();

	long getLength();

	float getCompletedRatio();

	long getElapsed();
}
