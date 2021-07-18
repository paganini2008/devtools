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

import java.io.File;
import java.io.IOException;

/**
 * 
 * DirectoryWalkerHandler
 *
 * @author Fred Feng
 * @since 1.0
 */
public interface DirectoryWalkerHandler {

	default void handleDirectoryStart(File file, int depth) throws IOException {
	}

	default void handleDirectoryEnd(File file, Directory directory, int depth) throws IOException {
	}

	default boolean handleDirectoryOnError(File file, int depth, Throwable e) {
		return true;
	}

	default boolean shouldHandleDirectory(File directory, int depth) throws IOException {
		return true;
	}

	default boolean shouldHandleFile(File file, int depth) throws IOException {
		return true;
	}

	void handleFile(File file, int depth) throws Exception;

	default boolean handleFileOnError(File file, int depth, Throwable e) {
		return true;
	}

}
