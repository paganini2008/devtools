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

/**
 * Base class of the file filters, supports combining filters with {@link #and(FileFilter)}, {@link #or(FileFilter)} and {@link #not()}. Subclasses must override at least one of the two <code>accept</code> methods.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public abstract class LogicalFileFilter implements FileFilter {

	/**
	 * Returns a filter accepting a file only if both this filter and the given one accept it.
	 */
	public LogicalFileFilter and(FileFilter filter) {
		return new AndFileFilter(this, filter);
	}

	/**
	 * Returns a filter accepting a file if this filter or the given one accepts it.
	 */
	public LogicalFileFilter or(FileFilter filter) {
		return new OrFileFilter(this, filter);
	}

	/**
	 * Returns a filter which negates this filter.
	 */
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
