package com.github.paganini2008.devtools;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.github.paganini2008.devtools.io.IOUtils;

/**
 * 
 * ExceptionUtils
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class ExceptionUtils {

	private ExceptionUtils() {
	}

	public static String toString(Throwable e) {
		PrintWriter writer = null;
		try {
			StringWriter out = new StringWriter();
			writer = new PrintWriter(out);
			e.printStackTrace(writer);
			return out.toString();
		} finally {
			IOUtils.closeQuietly(writer);
		}
	}

}
