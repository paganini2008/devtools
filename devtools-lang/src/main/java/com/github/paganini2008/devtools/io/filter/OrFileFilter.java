package com.github.paganini2008.devtools.io.filter;

import java.io.File;

/**
 * OrFileFilter
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class OrFileFilter extends LogicalFileFilter {

	public OrFileFilter(FileFilter leftFilter, FileFilter rightFilter) {
		this.leftFilter = leftFilter;
		this.rightFilter = rightFilter;
	}

	private final FileFilter leftFilter;

	private final FileFilter rightFilter;

	public boolean accept(File file) {
		return leftFilter.accept(file) || rightFilter.accept(file);
	}

	public boolean accept(File dir, String name) {
		return leftFilter.accept(dir, name) || rightFilter.accept(dir, name);
	}

	public static LogicalFileFilter create(FileFilter... filters) {
		LogicalFileFilter result = null;
		for (FileFilter filter : filters) {
			if (result != null) {
				result = result.or(filter);
			} else {
				result = new AndFileFilter(TrueFileFilter.INSTANCE, filter);
			}
		}
		return result;
	}

}
