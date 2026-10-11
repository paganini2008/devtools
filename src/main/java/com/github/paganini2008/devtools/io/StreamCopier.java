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

import java.io.IOException;

/**
 * 
 * Receives the chunks of data copied by {@link IOUtils#copy(java.io.InputStream, StreamCopier, int, int)}.
 * 
 * @author Fred Feng
 * @date 2021-03-11
 *
 * @since 2.0.1
 */
public interface StreamCopier {

	/**
	 * Called before the first chunk is copied.
	 */
	default void onStart() {
	}

	/**
	 * Receives the next chunk of data.
	 */
	void copy(byte[] bytes, int offset, int length) throws IOException;

	/**
	 * Called after each chunk with the total number of bytes copied so far.
	 */
	default void onProgress(long length) {
	}

	/**
	 * Called when copying has finished or failed, with the total number of bytes copied.
	 */
	default void onEnd(long length) {
	}

}
