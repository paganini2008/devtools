package com.github.paganini2008.devtools.io;

import java.io.File;

/**
 * 
 * Directory
 *
 * @author Fred Feng
 * @since 1.0
 */
public interface Directory {

	File getFile();

	Directory[] getChildren();

	long getLength();

	long getLastModified();

	int getFileCount(boolean recursive);

	int getFolderCount(boolean recursive);
	
	Directory getParent();

}
