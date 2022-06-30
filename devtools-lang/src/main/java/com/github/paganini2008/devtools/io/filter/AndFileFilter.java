/**
* Copyright 2017-2022 Fred Feng (paganini.fy@gmail.com)

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
package com.github.paganini2008.devtools.io.filter;

import java.io.File;

/**
 * AndFileFilter
 * 
 * @author Fred Feng
 * @since 2.0.1
 */
public class AndFileFilter extends LogicalFileFilter {

	public AndFileFilter(FileFilter leftFilter, FileFilter rightFilter) {
		this.leftFilter = leftFilter;
		this.rightFilter = rightFilter;
	}

	private final FileFilter leftFilter;
	private final FileFilter rightFilter;

	public boolean accept(File file) {
		return leftFilter.accept(file) && rightFilter.accept(file);
	}

	public boolean accept(File dir, String name) {
		return leftFilter.accept(dir, name) && rightFilter.accept(dir, name);
	}

	public static LogicalFileFilter create(FileFilter... filters) {
		LogicalFileFilter result = null;
		for (FileFilter filter : filters) {
			if (result != null) {
				result = result.and(filter);
			} else {
				result = new AndFileFilter(TrueFileFilter.INSTANCE, filter);
			}
		}
		return result;
	}

}
