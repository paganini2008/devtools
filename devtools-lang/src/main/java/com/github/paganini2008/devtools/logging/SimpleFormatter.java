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
package com.github.paganini2008.devtools.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * SimpleFormatter
 * 
 * @author Fred Feng
 * @since 2.0.1
 */
public class SimpleFormatter extends Formatter {

	private static final String lineSeparator = System.getProperty("line.separator");

	private static final SimpleDateFormat dateFormat;

	static {
		dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a z", Locale.getDefault());
	}

	public SimpleFormatter() {
	}

	public synchronized String format(LogRecord record) {
		StringBuffer info = new StringBuffer();
		info.append(dateFormat.format(record.getMillis()));
		info.append(": [");
		info.append(record.getLevel().getName().toUpperCase());
		info.append("] ");
		String message = formatMessage(record);
		info.append(message);
		info.append(lineSeparator);
		if (record.getThrown() != null) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			record.getThrown().printStackTrace(pw);
			pw.close();
			info.append(sw.toString());
		}
		return info.toString();
	}
}
