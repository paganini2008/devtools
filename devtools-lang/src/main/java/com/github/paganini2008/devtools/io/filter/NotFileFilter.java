package com.github.paganini2008.devtools.io.filter;

import java.io.File;

/**
 * NotFileFilter
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class NotFileFilter extends LogicalFileFilter {

	public NotFileFilter(FileFilter filter) {
		this.filter = filter;
	}

	private FileFilter filter;

	public boolean accept(File file) {
		return !filter.accept(file);
	}

	public boolean accept(File dir, String name) {
		return !filter.accept(dir, name);
	}

}
