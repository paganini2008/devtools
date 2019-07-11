package com.github.paganini2008.devtools;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import com.github.paganini2008.devtools.io.IOUtils;
import com.github.paganini2008.devtools.io.StringArrayWriter;

/**
 * 
 * ExceptionUtils
 * 
 * @author Fred Feng
 * @revised 2019-07
 * @created 2016-12
 * @version 1.0
 */
public class ExceptionUtils {

	private ExceptionUtils() {
	}
	
	public static String[] toArray(Throwable e) {
		if (e == null) {
			return new String[0];
		}
		String[] array = null;
		PrintWriter writer = null;
		try {
			StringArrayWriter out = new StringArrayWriter();
			writer = new PrintWriter(out);
			e.printStackTrace(writer);
			array = out.toArray();
		} finally {
			IOUtils.closeQuietly(writer);
		}
		List<String> results = new ArrayList<String>();
		for (String line : array) {
			if (line.equals(IOUtils.NEWLINE)) {
				continue;
			}
			if (line.startsWith("\t")) {
				line = line.replace("\t", "    ");
			}
			results.add(line);
		}
		return results.toArray(new String[0]);
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
