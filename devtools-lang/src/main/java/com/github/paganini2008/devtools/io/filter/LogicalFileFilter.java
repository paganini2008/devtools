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

/**
 * LogicalFileFilter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public abstract class LogicalFileFilter implements FileFilter {

	public LogicalFileFilter and(FileFilter filter) {
		return new AndFileFilter(this, filter);
	}

	public LogicalFileFilter or(FileFilter filter) {
		return new OrFileFilter(this, filter);
	}

	public LogicalFileFilter not() {
		return new NotFileFilter(this);
	}

	public boolean accept(File dir, String name) {
		return this.accept(new File(dir, name));
	}

	public boolean accept(File file) {
		return this.accept(file.getParentFile(), file.getName());
	}

}
