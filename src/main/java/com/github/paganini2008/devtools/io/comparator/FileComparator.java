/**
* Copyright 2017-2026 Fred Feng (paganini.fy@gmail.com)

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

import com.github.paganini2008.devtools.comparator.AbstractComparator;

/**
 * Base comparator of files which always sorts directories before regular files. Subclasses refine the order of two files of the same kind by overriding {@link #continueCompare(File, File)}.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public class FileComparator extends AbstractComparator<File> {

	public int compare(File left, File right) {
		int a = left.isDirectory() ? 1 : 2;
		int b = right.isDirectory() ? 1 : 2;
		if (a == b) {
			return continueCompare(left, right);
		}
		return a - b;
	}

	/**
	 * Compares two files of the same kind (both directories or both regular files). Returns 0 by default.
	 */
	protected int continueCompare(File left, File right) {
		return 0;
	}

}
