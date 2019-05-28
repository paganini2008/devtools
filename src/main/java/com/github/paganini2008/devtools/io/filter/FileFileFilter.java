package com.github.paganini2008.devtools.io.filter;

import java.io.File;

/**
 * FileFileFilter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class FileFileFilter extends LogicalFileFilter {

	public boolean accept(File file) {
		return file.isFile();
	}

}
