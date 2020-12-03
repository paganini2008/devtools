package com.github.paganini2008.devtools.io.filter;

import java.io.File;

/**
 * DirectoryFileFilter
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class DirectoryFileFilter extends LogicalFileFilter {

	public boolean accept(File file) {
		return file.isDirectory();
	}

}
