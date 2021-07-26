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
package com.github.paganini2008.devtools.io.filter;

import java.io.File;

import com.github.paganini2008.devtools.io.PathUtils;

/**
 * ExtensionFileFilter
 * 
 * @author Fred Feng
 * @since 2.0.1
 */
public class ExtensionFileFilter extends FileFileFilter {

	public ExtensionFileFilter(String extension) {
		this(extension, true);
	}

	public ExtensionFileFilter(String extension, boolean ignoreCase) {
		this.extension = extension;
		this.ignoreCase = ignoreCase;
	}

	private final String extension;
	private final boolean ignoreCase;

	public boolean accept(File file, String name) {
		if (super.accept(file)) {
			String ext = PathUtils.getExtension(name);
			return ignoreCase ? extension.equalsIgnoreCase(ext) : extension.equals(ext);
		}
		return false;
	}

}
