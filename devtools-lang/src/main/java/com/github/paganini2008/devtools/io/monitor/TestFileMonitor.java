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
package com.github.paganini2008.devtools.io.monitor;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class TestFileMonitor {

	public static void main(String[] args) throws Exception {
		File directory = new File("d:/sql/testDir");
		FileWatcher watcher = new FileWatcher(directory, -1, null);
		watcher.addListeners(new LoggingFileChangeListener());
		FileMonitor monitor = new FileMonitor(5, TimeUnit.SECONDS);
		monitor.addWatchers(watcher);
		monitor.start();
		System.in.read();
		monitor.stop();
	}

}
