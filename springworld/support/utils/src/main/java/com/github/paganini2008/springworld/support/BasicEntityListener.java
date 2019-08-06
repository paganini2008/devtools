package com.github.paganini2008.springworld.support;

import java.util.Date;

import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * BasicEntityListener
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-03
 * @version 1.0
 */
public class BasicEntityListener {

	private static final Logger logger = LoggerFactory.getLogger(BasicEntityListener.class);

	@PrePersist
	public void prePersist(Object bean) {
		if (!(bean instanceof BaseEntity)) {
			return;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Persist entity: " + bean);
		}
		BaseEntity entity = (BaseEntity) bean;
		if (entity.getId() == null) {
			IdGenerator idGenerator = ApplicationContextUtils.getBean(IdGenerator.class);
			entity.setId(idGenerator.nextValue());
		}
		if (entity.getCreateDate() == null) {
			entity.setCreateDate(new Date());
		}
		if (entity.getUpdateDate() == null) {
			entity.setUpdateDate(new Date());
		}
	}

	@PreUpdate
	public void preUpdate(Object bean) {
		if (!(bean instanceof BaseEntity)) {
			return;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Update entity: " + bean);
		}
		BaseEntity entity = (BaseEntity) bean;
		entity.setUpdateDate(new Date());
	}

	@PreRemove
	public void preRemove(Object bean) {
		if (!(bean instanceof BaseEntity)) {
			return;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Remove entity: " + bean);
		}
	}

}
