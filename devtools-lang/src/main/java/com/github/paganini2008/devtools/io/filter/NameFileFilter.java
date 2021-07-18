/**
* Copyright 2018-2021 Fred Feng (paganini.fy@gmail.com)

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

import com.github.paganini2008.devtools.io.PathUtils;

/**
 * NameFileFilter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class NameFileFilter extends LogicalFileFilter {

	public NameFileFilter(String str, boolean ignoreCase) {
		this.str = str;
		this.ignoreCase = ignoreCase;
	}

	private final String str;
	private final boolean ignoreCase;

	public boolean accept(File file, String name) {
		String base = PathUtils.getBaseName(name);
		return ignoreCase ? base.equalsIgnoreCase(str) : base.equals(str);
	}

}
