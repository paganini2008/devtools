/**
* Copyright 2018-2021 Fred Feng (paganini.fy@gmail.com)

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

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * StringArrayWriter
 * 
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public class StringArrayWriter extends Writer {

	private final List<String> lines = new ArrayList<String>();

	public void write(String str) {
		lines.add(str);
	}

	public void write(char[] cbuf, int off, int len) throws IOException {
		lines.add(new String(cbuf, off, len));
	}

	public void flush() throws IOException {
	}

	public void close() throws IOException {
		lines.clear();
	}

	public String[] toArray() {
		return lines.toArray(new String[0]);
	}

}
