package com.github.paganini2008.devtools.io;

import java.io.File;
import java.io.IOException;

/**
 * FileWatcher
 * 
 * @author Fred Feng
 * @version 1.0
 */
public interface FileWatcher {

	void onCreate(File file) throws IOException;

	void onDelete(File file) throws IOException;

	void onUpdate(File file) throws IOException;

}
