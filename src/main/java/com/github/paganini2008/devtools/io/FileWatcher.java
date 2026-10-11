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
 * Listener notified by a {@link FileWatchdog} about file changes.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public interface FileWatcher {

	/**
	 * Called when the watched file has been created.
	 */
	void onCreate(File file) throws IOException;

	/**
	 * Called when the watched file has been deleted.
	 */
	void onDelete(File file) throws IOException;

	/**
	 * Called when the length or the modification time of the watched file has changed.
	 */
	void onUpdate(File file) throws IOException;

}
