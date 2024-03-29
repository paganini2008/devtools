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
package com.github.paganini2008.devtools.io;

import java.io.File;

/**
 * 
 * TreeMatcher
 *
 * @author Fred Feng
 * @version 2.0.5
 */
public interface TreeMatcher {

	default boolean matchDirectory(File directory, int depth, boolean hasLast) {
		return true;
	}

	default boolean matchFile(File directory, int depth, File file, boolean hasLast) {
		return true;
	}

	String getText(File directory, int depth, boolean hasLast);

	String getText(File directory, int depth, File file, boolean hasLast);

}
