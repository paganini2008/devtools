package com.github.paganini2008.devtools.io;

import java.io.File;

import com.github.paganini2008.devtools.StringUtils;

/**
 * 
 * DefaultTreeMatcher
 *
 * @author Fred Feng
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
