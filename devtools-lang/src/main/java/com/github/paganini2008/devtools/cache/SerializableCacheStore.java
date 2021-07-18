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
package com.github.paganini2008.devtools.cache;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import com.github.paganini2008.devtools.collection.LruMap;
import com.github.paganini2008.devtools.io.FileUtils;
import com.github.paganini2008.devtools.io.SerializationUtils;
import com.github.paganini2008.devtools.logging.Log;
import com.github.paganini2008.devtools.logging.LogFactory;

/**
 * SerializableCacheStore
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class SerializableCacheStore implements CacheStore {

	private static final Log logger = LogFactory.getLog(SerializableCacheStore.class);

	public static final String fileExtension = ".cache";

	public SerializableCacheStore(int maxSize) {
		cache = new LruMap<Object, File>(maxSize) {

			private static final long serialVersionUID = 1L;

			public void onEviction(Object element, File file) {
				try {
					FileUtils.deleteFile(file);
					if (logger.isDebugEnabled()) {
						logger.debug("Remove object from disk: " + file);
					}
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		};
	}

	private String storeDir = FileUtils.getTempDirectoryPath();

	public void setStoreDir(String storeDir) {
		this.storeDir = storeDir;
	}

	private final LruMap<Object, File> cache;

	protected File asFile(String name) {
		String fileName = UUID.nameUUIDFromBytes(name.getBytes()).toString();
		return FileUtils.getFile(storeDir, fileName + fileExtension);
	}

	public void writeObject(Object name, Object o) {
		File file = asFile(name.toString());
		SerializationUtils.writeObject(o, file, false);
		cache.put(name, file);
		if (logger.isDebugEnabled()) {
			logger.debug("Write object to disk: " + file);
		}
	}

	public Object readObject(Object name) {
		File file = asFile(name.toString());
		if (logger.isDebugEnabled()) {
			logger.debug("Read object from disk: " + file);
		}
		return SerializationUtils.readObject(file, false);
	}

	public Object removeObject(Object name) {
		Object obj = readObject(name);
		if (obj != null) {
			File file = asFile(name.toString());
			if (logger.isDebugEnabled()) {
				logger.debug("Remove object from disk: " + file);
			}
			try {
				FileUtils.deleteFile(file);
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
			cache.remove(name);
		}
		return obj;
	}

	public int getSize() {
		return cache.size();
	}

	public Set<Object> keys() {
		return Collections.unmodifiableSet(cache.keySet());
	}

	@Override
	public boolean hasKey(Object key) {
		return cache.containsKey(key);
	}

}
