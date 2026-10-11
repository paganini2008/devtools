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
package com.github.paganini2008.devtools.io.monitor;

/**
 * Callback interface notified by a {@link FileWatcher} when files or directories are created, updated or deleted.
 *
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public interface FileChangeListener {

	/**
	 * Called before each check of the watcher.
	 */
	default void onStart(FileWatcher fileWatcher) {
	}

	/**
	 * Called after each check of the watcher.
	 */
	default void onEnd(FileWatcher fileWatcher) {
	}

	/**
	 * Called when a directory is created. Parents are notified before their children.
	 */
	void onDirectoryCreate(FileEntry fileEntry);

	/**
	 * Called when a file is created.
	 */
	void onFileCreate(FileEntry fileEntry);

	/**
	 * Called when a directory is deleted. Parents are notified before their children.
	 */
	void onDirectoryDelete(FileEntry fileEntry);

	/**
	 * Called when a file is deleted.
	 */
	void onFileDelete(FileEntry fileEntry);

	/**
	 * Called when the attributes (e.g. last modified time) of a directory changed.
	 *
	 * @param lastFileEntry the previous snapshot
	 * @param fileEntry the current snapshot
	 */
	void onDirectoryUpdate(FileEntry lastFileEntry, FileEntry fileEntry);

	/**
	 * Called when the attributes (e.g. length or last modified time) of a file changed.
	 *
	 * @param lastFileEntry the previous snapshot
	 * @param fileEntry the current snapshot
	 */
	void onFileUpdate(FileEntry lastFileEntry, FileEntry fileEntry);

}
