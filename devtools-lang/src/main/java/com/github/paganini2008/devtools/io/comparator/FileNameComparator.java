package com.github.paganini2008.devtools.io.comparator;

import java.io.File;

import com.github.paganini2008.devtools.comparator.ComparatorHelper;
import com.github.paganini2008.devtools.io.FileUtils;

/**
 * NameFileComparator
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class FileNameComparator extends FileComparator {

	private final boolean ignoreCase;

	public FileNameComparator() {
		this(true);
	}

	public FileNameComparator(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}

	protected int continueCompare(File left, File right) {
		String leftName = FileUtils.getBaseName(left);
		String rightName = FileUtils.getBaseName(right);
		return ComparatorHelper.compareTo(leftName, rightName, ignoreCase);
	}

}
