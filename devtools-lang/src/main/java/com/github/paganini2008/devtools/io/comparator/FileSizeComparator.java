package com.github.paganini2008.devtools.io.comparator;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import com.github.paganini2008.devtools.comparator.ComparatorHelper;
import com.github.paganini2008.devtools.io.FileAssert;
import com.github.paganini2008.devtools.io.FileUtils;

/**
 * 
 * FileSizeComparator
 * 
 * @author Fred Feng
 * 
 * @version 1.0
 */
public class FileSizeComparator extends FileComparator {

	private final FileFilter fileFilter;

	public FileSizeComparator(FileFilter fileFilter) {
		this.fileFilter = fileFilter;
	}

	protected int continueCompare(File left, File right) {
		long leftSize = 0, rightSize = 0;
		try {
			leftSize = sizeOf(left, fileFilter);
			rightSize = sizeOf(right, fileFilter);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		return ComparatorHelper.valueOf(leftSize - rightSize);
	}

	private long sizeOf(File file, FileFilter filter) throws IOException {
		FileAssert.notExisted(file);
		if (file.isDirectory()) {
			return FileUtils.sizeOfDirectory(file, filter);
		} else {
			return file.length();
		}
	}

}
