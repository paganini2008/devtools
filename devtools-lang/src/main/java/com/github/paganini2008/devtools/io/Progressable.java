package com.github.paganini2008.devtools.io;

/**
 * 
 * Progressable
 * 
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public interface Progressable {

	void progress(int fileCount, int folderCount, long length, float completedRatio, long elapsed);

}
