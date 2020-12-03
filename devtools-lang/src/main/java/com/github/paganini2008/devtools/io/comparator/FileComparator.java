package com.github.paganini2008.devtools.io.comparator;

import java.io.File;

import com.github.paganini2008.devtools.comparator.AbstractComparator;

/**
 * FileComparator
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class FileComparator extends AbstractComparator<File> {

	public FileComparator() {
	}

	public int compare(File left, File right) {
		int a = left.isDirectory() ? 1 : 2;
		int b = right.isDirectory() ? 1 : 2;
		if (a == b) {
			return continueCompare(left, right);
		}
		return a - b;
	}

	protected int continueCompare(File left, File right) {
		return 0;
	}

}
