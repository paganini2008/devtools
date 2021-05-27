package com.github.paganini2008.devtools.cache;

import java.util.Set;

/**
 * 
 * MasterStandbyCache
 * 
 * @author Fred Feng
 * 
 * @version 1.0
 */
public class MasterStandbyCache extends AbstractCache {

	private final Cache master;
	private final Cache backup;

	public MasterStandbyCache(Cache master, Cache backup) {
		this.master = master;
		this.backup = backup;
	}

	public void putObject(Object key, Object value, boolean ifAbsent) {
		backup.putObject(key, value, ifAbsent);
		master.putObject(key, value, ifAbsent);
	}

	public boolean hasKey(Object key) {
		return master.hasKey(key) ? true : backup.hasKey(key);
	}

	public Object getObject(Object key) {
		Object result = master.getObject(key);
		if (result == null) {
			result = backup.getObject(key);
			if (result != null) {
				master.putObject(key, result);
			}
		}
		return result;
	}

	public Object removeObject(Object key) {
		backup.removeObject(key);
		return master.removeObject(key);
	}

	public Set<Object> keys() {
		Set<Object> keys = master.keys();
		if (keys == null) {
			keys = backup.keys();
		}
		return keys;
	}

	public void clear() {
		backup.clear();
		master.clear();
	}

	public int getSize() {
		return Math.max(master.getSize(), backup.getSize());
	}

}
