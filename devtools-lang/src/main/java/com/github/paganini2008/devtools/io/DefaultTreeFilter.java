package com.github.paganini2008.devtools.io;

import java.io.File;

import com.github.paganini2008.devtools.StringUtils;

/**
 * 
 * DefaultTreeFilter
 *
 * @author Fred Feng
 * @version 1.0.0
 */
public class DefaultTreeFilter implements TreeFilter {

	@Override
	public String getText(File directory, int depth) {
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
		return str.toString();
	}

	@Override
	public String getText(File directory, int depth, File file) {
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
		return str.toString();
	}

}
