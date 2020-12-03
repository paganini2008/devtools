package com.github.paganini2008.devtools.io.filter;

import java.io.File;

/**
 * LogicalFileFilter
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public abstract class LogicalFileFilter implements FileFilter {

	public LogicalFileFilter and(FileFilter filter) {
		return new AndFileFilter(this, filter);
	}

	public LogicalFileFilter or(FileFilter filter) {
		return new OrFileFilter(this, filter);
	}

	public LogicalFileFilter not() {
		return new NotFileFilter(this);
	}

	public boolean accept(File dir, String name) {
		return this.accept(new File(dir, name));
	}

	public boolean accept(File file) {
		return this.accept(file.getParentFile(), file.getName());
	}

}
