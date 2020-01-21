package com.github.paganini2008.springworld.cluster;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.annotation.Autowired;

import com.github.paganini2008.devtools.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * ClusterId
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
@Slf4j
public final class ClusterId {

	@Autowired
	private ClusterIdGenerator idGenerator;

	@Autowired
	private ContextClusterConfigProperties configProperties;

	private final AtomicBoolean master = new AtomicBoolean(false);

	public String get() {
		if (StringUtils.isBlank(configProperties.getId())) {
			synchronized (this) {
				if (StringUtils.isBlank(configProperties.getId())) {
					configProperties.setId(idGenerator.generateClusterId());
					log.info("\n\tClusterId: " + configProperties.getId());
				}
			}
		}
		return configProperties.getId();
	}

	public boolean isMaster() {
		return master.get();
	}

	public void setMaster(boolean master) {
		this.master.set(master);
	}

	public String toString() {
		return "ClusterId: " + get() + ", Master: " + isMaster();
	}

}
