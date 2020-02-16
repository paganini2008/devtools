package com.github.paganini2008.devtools.io;

import java.io.File;

/**
 * 
 * FileInfo
 * 
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public interface FileInfo {

	File getFile();

	int getFileCount();

	int getFolderCount();

	long getLength();

	float getCompletedRatio();

	long getElapsed();

	boolean isFinished();
}
