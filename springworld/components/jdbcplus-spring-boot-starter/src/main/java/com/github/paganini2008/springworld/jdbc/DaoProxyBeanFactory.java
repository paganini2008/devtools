package com.github.paganini2008.springworld.jdbc;

import java.lang.reflect.Proxy;

import org.springframework.beans.factory.FactoryBean;

/**
 * 
 * DaoProxyBeanFactory
 *
 * @author Fred Feng
 * @created 2019-10
 * @revised 2020-01
 * @version 1.0
 */
public class DaoProxyBeanFactory<T> implements FactoryBean<T> {

	private final Class<T> interfaceClass;

	public DaoProxyBeanFactory(Class<T> interfaceClass) {
		this.interfaceClass = interfaceClass;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getObject() throws Exception {
		return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[] { interfaceClass },
				new DaoProxyBean<T>(interfaceClass));
	}

	@Override
	public Class<?> getObjectType() {
		return interfaceClass;
	}

}
