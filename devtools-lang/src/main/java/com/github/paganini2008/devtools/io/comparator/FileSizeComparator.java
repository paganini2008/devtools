/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
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
