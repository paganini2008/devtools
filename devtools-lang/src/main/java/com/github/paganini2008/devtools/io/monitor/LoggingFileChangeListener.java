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
package com.github.paganini2008.devtools.io.monitor;

import com.github.paganini2008.devtools.logging.Log;
import com.github.paganini2008.devtools.logging.LogFactory;

/**
 * 
 * LoggingFileChangeListener
 * 
 * @author Fred Feng
 * 
 * @since 2.0.1
 */
public class LoggingFileChangeListener implements FileChangeListener {

	private static final Log logger = LogFactory.getLog(LoggingFileChangeListener.class);

	public void onStart(FileWatcher fileMonitor) {
		logger.info("onStart!");
	}

	public void onDirectoryCreate(FileEntry fileEntry) {
		logger.info("onDirectoryCreate: " + fileEntry);
	}

	public void onFileCreate(FileEntry fileEntry) {
		logger.info("onFileCreate: " + fileEntry);
	}

	public void onDirectoryDelete(FileEntry fileEntry) {
		logger.info("onDirectoryDelete: " + fileEntry);
	}

	public void onFileDelete(FileEntry fileEntry) {
		logger.info("onFileDelete: " + fileEntry);
	}

	public void onDirectoryUpdate(FileEntry lastFileEntry, FileEntry fileEntry) {
		logger.info("onDirectoryUpdate last: " + lastFileEntry);
		logger.info("onDirectoryUpdate new: " + fileEntry);
	}

	public void onFileUpdate(FileEntry lastFileEntry, FileEntry fileEntry) {
		logger.info("onFileUpdate last: " + lastFileEntry);
		logger.info("onFileUpdate new: " + fileEntry);
	}

	public void onEnd(FileWatcher fileMonitor) {
		logger.info("onEnd!");
	}

}
