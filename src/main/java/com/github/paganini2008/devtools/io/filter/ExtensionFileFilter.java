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
package com.github.paganini2008.devtools.io.filter;

import java.io.File;

import com.github.paganini2008.devtools.io.PathUtils;

/**
 * A filter which accepts regular files with the given extension (a leading dot is optional).
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public class ExtensionFileFilter extends FileFileFilter {

	public ExtensionFileFilter(String extension) {
		this(extension, true);
	}

	public ExtensionFileFilter(String extension, boolean ignoreCase) {
		if (extension == null) {
			throw new IllegalArgumentException("Extension must not be null.");
		}
		this.extension = extension.startsWith(".") ? extension.substring(1) : extension;
		this.ignoreCase = ignoreCase;
	}

	private final String extension;
	private final boolean ignoreCase;

	public boolean accept(File file) {
		if (super.accept(file)) {
			String ext = PathUtils.getExtension(file.getName());
			return ignoreCase ? extension.equalsIgnoreCase(ext) : extension.equals(ext);
		}
		return false;
	}

	public boolean accept(File dir, String name) {
		return accept(new File(dir, name));
	}

}
