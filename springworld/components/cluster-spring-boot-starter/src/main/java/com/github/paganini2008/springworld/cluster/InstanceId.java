package com.github.paganini2008.springworld.cluster;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.annotation.Value;

import com.github.paganini2008.devtools.StringUtils;

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
	private final AtomicBoolean master;

	public InstanceId(InstanceIdGenerator idGenerator) {
		this.idGenerator = idGenerator;
		this.master = new AtomicBoolean(false);
	}

	@Value("${spring.application.id:}")
	private String id;

	@Value("${spring.application.cluster.weight:1}")
	private int weight;

	public String get() {
		if (StringUtils.isBlank(id)) {
			synchronized (this) {
				if (StringUtils.isBlank(id)) {
					id = idGenerator.generateInstanceId();
					log.info("Create instanceId of cluster: " + id);
				}
			}
		}
		return id;
	}

	public int getWeight() {
		return weight;
	}

	public boolean isMaster() {
		return master.get();
	}

	public void setMaster(boolean master) {
		this.master.set(master);
	}

	public String toString() {
		return "InstanceId: " + get() + ", Master: " + isMaster();
	}

}
