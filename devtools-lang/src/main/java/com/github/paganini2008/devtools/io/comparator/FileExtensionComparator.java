package com.github.paganini2008.devtools.io.comparator;

import java.io.File;

import com.github.paganini2008.devtools.comparator.ComparatorHelper;
import com.github.paganini2008.devtools.io.FileUtils;

/**
 * FileExtensionComparator
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class FileExtensionComparator extends FileComparator {

	private final boolean ignoreCase;

	public FileExtensionComparator() {
		this(true);
	}

	public FileExtensionComparator(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}

	protected int com(File left, File right) {
			String leftExt = FileUtils.getExtension(left);
			String rightExt = FileUtils.getExtension(right);
			return ComparatorHelper.compareTo(leftExt, rightExt, ignoreCase);
	}

}
