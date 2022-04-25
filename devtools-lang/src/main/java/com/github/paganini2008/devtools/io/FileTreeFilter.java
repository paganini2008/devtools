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
package com.github.paganini2008.devtools.io;

import static com.github.paganini2008.devtools.StringUtils.EMPTY_ARRAY;

import java.io.File;

import com.github.paganini2008.devtools.ArrayUtils;
import com.github.paganini2008.devtools.MatchMode;
import com.github.paganini2008.devtools.StringUtils;

/**
 * 
 * FileTreeFilter
 *
 * @author Fred Feng
 * @version 2.0.5
 */
public class FileTreeFilter extends DefaultTreeFilter {

	private final String[] includedFiles;
	private final String[] excludedFiles;
	private final MatchMode matchMode;

	public FileTreeFilter() {
		this(EMPTY_ARRAY, EMPTY_ARRAY, MatchMode.ANY_WHERE);
	}

	public FileTreeFilter(String includedFile, String excludedFile, MatchMode matchMode) {
		this(StringUtils.isNotBlank(includedFile) ? new String[] { includedFile } : EMPTY_ARRAY,
				StringUtils.isNotBlank(excludedFile) ? new String[] { excludedFile } : EMPTY_ARRAY, matchMode);
	}

	public FileTreeFilter(String[] includedFiles, String[] excludedFiles, MatchMode matchMode) {
		this.includedFiles = includedFiles;
		this.excludedFiles = excludedFiles;
		this.matchMode = matchMode;
	}

	@Override
	public boolean matchFile(File directory, int depth, File file) {
		boolean match = true;
		if (ArrayUtils.isNotEmpty(includedFiles)) {
			match = false;
			for (String pattern : includedFiles) {
				if (matchMode.matches(pattern, file.getName())) {
					match = true;
					break;
				}
			}
		}
		if (ArrayUtils.isNotEmpty(excludedFiles)) {
			for (String pattern : excludedFiles) {
				if (matchMode.matches(pattern, file.getName())) {
					match = false;
					break;
				}
			}
		}
		return match;
	}

}
