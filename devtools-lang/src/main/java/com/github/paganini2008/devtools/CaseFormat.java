package com.github.paganini2008.devtools;

/**
 * 
 * CaseFormat
 *
 * @author Fred Feng
 * @version 1.0
 */
public interface CaseFormat {

	String toCase(CharSequence str);

	public static CaseFormat DEFAULT = (str) -> {
		return str.toString();
	};

}
