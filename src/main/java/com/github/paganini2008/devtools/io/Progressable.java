package com.github.paganini2008.devtools.io;

/**
 * 
 * Progressable
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @created 2013-06
 * @version 1.0
 */
public interface Progressable {

	void progress(String processingFile, int fileCount, int folderCount, long length, float completedRatio, long elapsed);

}
