package com.github.paganini2008.devtools.io.comparator;

import java.io.File;

import com.github.paganini2008.devtools.comparator.ComparatorHelper;

/**
 * FilePathComparator
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class FilePathComparator extends FileComparator {

	private final boolean ignoreCase;

	public FilePathComparator() {
		this(true);
	}

	public FilePathComparator(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}

	protected int continueCompare(File left, File right) {
		return ComparatorHelper.compareTo(left.getAbsolutePath(), right.getAbsolutePath(), ignoreCase);
	}
}
