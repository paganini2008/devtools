package com.github.paganini2008.springworld.cluster.implementor;

/**
 * 
 * InstanceId
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
public final class InstanceId {

	private final InstanceIdGenerator idGenerator;

	public InstanceId(InstanceIdGenerator idGenerator) {
		this.idGenerator = idGenerator;
	}

	private String id;

	public String get() {
		if (id == null) {
			id = idGenerator.generateInstanceId();
		}
		return id;
	}

}
