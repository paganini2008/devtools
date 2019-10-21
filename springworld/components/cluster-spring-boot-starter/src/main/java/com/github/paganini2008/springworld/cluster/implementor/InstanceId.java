package com.github.paganini2008.springworld.cluster.implementor;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * InstanceId
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
@Slf4j
public final class InstanceId {

	private final InstanceIdGenerator idGenerator;

	public InstanceId(InstanceIdGenerator idGenerator) {
		this.idGenerator = idGenerator;
	}

	private String id;

	public String get() {
		if (id == null) {
			synchronized (this) {
				if (id == null) {
					id = idGenerator.generateInstanceId();
					log.info("Create instanceId of cluster: " + id);
				}
			}
		}
		return id;
	}

}
