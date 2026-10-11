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
package com.github.paganini2008.devtools.io;

import java.io.File;

import com.github.paganini2008.devtools.StringUtils;

/**
 * 
 * {@link TreeMatcher} that accepts every file and directory and renders each entry as an indented tree line such as
 * {@code "|    |--- name"}.
 *
 * @author Fred Feng
 * @date 2022-04-24
 * @version 1.0.0
 */
public class DefaultTreeMatcher implements TreeMatcher {

	private static final String NEWLINE = System.getProperty("line.separator");

	@Override
	public String getText(File directory, int depth, boolean hasLast) {
		int n = depth + 1;
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < n; i++) {
			str.append("|");
			if (i != n - 1) {
				str.append(StringUtils.repeat(' ', 4));
			} else {
				str.append("--- ");
			}
		}
		str.append(directory.getName());
		str.append(NEWLINE);
		return str.toString();
	}

	@Override
	public String getText(File directory, int depth, File file, boolean hasLast) {
		int n = depth + 1;
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < n; i++) {
			str.append("|");
			if (i != n - 1) {
				str.append(StringUtils.repeat(' ', 4));
			} else {
				str.append("--- ");
			}
		}
		str.append(file.getName());
		str.append(NEWLINE);
		return str.toString();
	}

}
