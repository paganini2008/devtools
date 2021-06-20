/**
* Copyright 2021 Fred Feng (paganini.fy@gmail.com)

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
 * NameFileComparator
 * 
 * @author Fred Feng
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
