package com.github.paganini2008.devtools.io;

/**
 * 
 * Progressable
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @version 1.0
 */
public interface Progressable {
	void progress(int fileCount, int folderCount, long length, long queueSize, String completedRatio, long elapsed);
}
