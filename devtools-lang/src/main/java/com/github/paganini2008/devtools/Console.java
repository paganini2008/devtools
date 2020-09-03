package com.github.paganini2008.devtools;

/**
 * 
 * Console
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
public abstract class Console {

	public static void log(String msg) {
		if (StringUtils.isNotBlank(msg)) {
			System.out.println(msg);
		}
	}

	public static void logf(String msg, Object... args) {
		if (StringUtils.isNotBlank(msg)) {
			System.out.printf(msg, args);
			System.out.println();
		}
	}

}
