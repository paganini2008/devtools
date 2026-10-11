/**
* Copyright 2017-2026 Fred Feng (paganini.fy@gmail.com)

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
 * Callback interface of a {@link DirectoryWalker}. Implementations may be called concurrently by several threads.
 *
 * @author Fred Feng
 * @date 2020-05-09
 * @since 2.0.1
 */
public interface DirectoryWalkerHandler {

	/**
	 * Called before the children of a directory are processed.
	 */
	default void handleDirectoryStart(File file, int depth) throws IOException {
	}

	/**
	 * Called after the children of a directory have been listed and processed.
	 */
	default void handleDirectoryEnd(File file, Directory directory, int depth) throws IOException {
	}

	/**
	 * Called when processing a directory fails.
	 *
	 * @return {@code true} to continue the walk, {@code false} to cancel it
	 */
	default boolean handleDirectoryOnError(File file, int depth, Throwable e) {
		return true;
	}

	/**
	 * Decides whether a directory is processed at all.
	 */
	default boolean shouldHandleDirectory(File directory, int depth) throws IOException {
		return true;
	}

	/**
	 * Decides whether {@link #handleFile(File, int)} is called for a file.
	 */
	default boolean shouldHandleFile(File file, int depth) throws IOException {
		return true;
	}

	/**
	 * Processes a file.
	 */
	void handleFile(File file, int depth) throws Exception;

	/**
	 * Called when {@link #handleFile(File, int)} fails.
	 *
	 * @return {@code true} to continue the walk, {@code false} to cancel it
	 */
	default boolean handleFileOnError(File file, int depth, Throwable e) {
		return true;
	}

}
