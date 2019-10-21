package com.github.paganini2008.springworld.socketbird.store;

/**
 * 
 * MemoryStoreFactory
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
public class MemoryStoreFactory implements StoreFactory {

	@Override
	public Store getObject() throws Exception {
		return new MemoryStore();
	}

	@Override
	public Class<?> getObjectType() {
		return MemoryStore.class;
	}

}
