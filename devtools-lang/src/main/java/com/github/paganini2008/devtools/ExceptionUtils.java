/**
* Copyright 2021 Fred Feng (paganini.fy@gmail.com)

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
 * @version 1.0
 */
public abstract class ExceptionUtils {

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

	public static boolean ignoreException(Throwable e, Class<?>[] exceptionClasses) {
		if (ArrayUtils.isNotEmpty(exceptionClasses)) {
			for (Class<?> exceptionClass : exceptionClasses) {
				if (exceptionClass.isAssignableFrom(e.getClass())) {
					return true;
				}
			}
		}
		return false;
	}

}
