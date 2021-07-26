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

import com.github.paganini2008.devtools.comparator.ComparatorHelper;
import com.github.paganini2008.devtools.io.FileUtils;

/**
 * FileExtensionComparator
 * 
 * @author Fred Feng
 * @since 2.0.1
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
