package com.github.paganini2008.devtools.io;

import static com.github.paganini2008.devtools.StringUtils.EMPTY_ARRAY;

import java.io.File;

import com.github.paganini2008.devtools.ArrayUtils;
import com.github.paganini2008.devtools.MatchMode;
import com.github.paganini2008.devtools.StringUtils;

/**
 * 
 * DirectoryTreeFilter
 *
 * @author Fred Feng
 * @version 1.0.0
 */
public class DirectoryTreeFilter extends DefaultTreeFilter {

	private final String[] includedFiles;
	private final String[] excludedFiles;
	private final MatchMode matchMode;

	public DirectoryTreeFilter() {
		this(EMPTY_ARRAY, EMPTY_ARRAY, MatchMode.ANY_WHERE);
	}

	public DirectoryTreeFilter(String includedFile, String excludedFile, MatchMode matchMode) {
		this(StringUtils.isNotBlank(includedFile) ? new String[] { includedFile } : EMPTY_ARRAY,
				StringUtils.isNotBlank(excludedFile) ? new String[] { excludedFile } : EMPTY_ARRAY, matchMode);
	}

	public DirectoryTreeFilter(String[] includedFiles, String[] excludedFiles, MatchMode matchMode) {
		this.includedFiles = includedFiles;
		this.excludedFiles = excludedFiles;
		this.matchMode = matchMode;
	}

	@Override
	public boolean matchDirectory(File directory, int depth) {
		boolean match = true;
		if (ArrayUtils.isNotEmpty(includedFiles)) {
			match = false;
			for (String pattern : includedFiles) {
				if (matchMode.matches(pattern, directory.getName())) {
					match = true;
					break;
				}
			}
		}
		if (ArrayUtils.isNotEmpty(excludedFiles)) {
			for (String pattern : excludedFiles) {
				if (matchMode.matches(pattern, directory.getName())) {
					match = false;
					break;
				}
			}
		}
		return match;
	}

	@Override
	public boolean matchFile(File directory, int depth, File file) {
		return false;
	}

}
