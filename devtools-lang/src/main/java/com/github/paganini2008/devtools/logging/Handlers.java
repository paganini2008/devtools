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
package com.github.paganini2008.devtools.logging;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.SocketHandler;

/**
 * 
 * Handlers
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class Handlers {

	public static Handler socketHandler(String host, int port, String level) throws IOException {
		SocketHandler handler = new SocketHandler(host, port);
		handler.setLevel(Levels.getByName(level));
		return handler;
	}

	public static Handler fileHandler(String pattern, int limit, String level) throws IOException {
		return fileHandler(pattern, limit, 1, true, level);
	}

	public static Handler fileHandler(String pattern, int limit, int count, boolean append, String level) throws IOException {
		FileHandler handler = new FileHandler(pattern, limit, count, append);
		handler.setLevel(Levels.getByName(level));
		return handler;
	}
	
	private Handlers() {
	}

}
