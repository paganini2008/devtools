package com.github.paganini2008.devtools.cache;

/**
 * 
 * BoundedCache
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public abstract class BoundedCache extends AbstractCache implements Cache {

	public abstract void setCacheStore(CacheStore store);

	protected void dispose(Object eldestKey, Object eldestObject) {
	}

}
