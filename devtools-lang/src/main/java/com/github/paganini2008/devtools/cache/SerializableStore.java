package com.github.paganini2008.devtools.cache;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.github.paganini2008.devtools.collection.LruMap;
import com.github.paganini2008.devtools.io.FileUtils;
import com.github.paganini2008.devtools.io.SerializationUtils;
import com.github.paganini2008.devtools.logging.Log;
import com.github.paganini2008.devtools.logging.LogFactory;

/**
 * SerializableStore
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class SerializableStore implements Store {

	private static final Log logger = LogFactory.getLog(SerializableStore.class);

	public static final String fileExtension = ".cache";

	public SerializableStore(int maxSize) {
		cache = new LruMap<Object, File>(maxSize) {

			private static final long serialVersionUID = 8262093056909008093L;

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
		return new HashSet<Object>(cache.keySet());
	}

}
