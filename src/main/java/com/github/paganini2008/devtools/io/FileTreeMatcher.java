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

import static com.github.paganini2008.devtools.StringUtils.EMPTY_ARRAY;

import java.io.File;

import com.github.paganini2008.devtools.ArrayUtils;
import com.github.paganini2008.devtools.MatchMode;
import com.github.paganini2008.devtools.StringUtils;

/**
 * 
 * {@link TreeMatcher} that renders directories and files, optionally filtering files by include and exclude patterns
 * matched against the file name.
 *
 * @author Fred Feng
 * @date 2022-04-24
 * @version 2.0.5
 */
public class FileTreeMatcher extends DefaultTreeMatcher {

	private final String[] includedFiles;
	private final String[] excludedFiles;
	private final MatchMode matchMode;

	public FileTreeMatcher() {
		this(EMPTY_ARRAY, EMPTY_ARRAY, MatchMode.ANY_WHERE);
	}

	public FileTreeMatcher(String includedFile, String excludedFile, MatchMode matchMode) {
		this(StringUtils.isNotBlank(includedFile) ? new String[] { includedFile } : EMPTY_ARRAY,
				StringUtils.isNotBlank(excludedFile) ? new String[] { excludedFile } : EMPTY_ARRAY, matchMode);
	}

	/**
	 * Creates a matcher; a file matches if its name matches one of the included patterns (or none are given) and none of the
	 * excluded patterns.
	 */
	public FileTreeMatcher(String[] includedFiles, String[] excludedFiles, MatchMode matchMode) {
		this.includedFiles = includedFiles;
		this.excludedFiles = excludedFiles;
		this.matchMode = matchMode;
	}

	@Override
	public boolean matchFile(File directory, int depth, File file, boolean hasLast) {
		boolean match = true;
		if (ArrayUtils.isNotEmpty(includedFiles)) {
			match = false;
			for (String pattern : includedFiles) {
				if (matchMode.matches(file.getName(), pattern)) {
					match = true;
					break;
				}
			}
		}
		if (ArrayUtils.isNotEmpty(excludedFiles)) {
			for (String pattern : excludedFiles) {
				if (matchMode.matches(file.getName(), pattern)) {
					match = false;
					break;
				}
			}
		}
		return match;
	}

}
