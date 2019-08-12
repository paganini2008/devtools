package com.github.paganini2008.springworld.fastjpa;

/**
 * 
 * JpaAttributeDetail
 * 
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-02
 * @version 1.0
 */
public interface JpaAttributeDetail {

	String getName();

	Class<?> getJavaType();

	boolean isId();

	boolean isVersion();

	boolean isOptional();

	boolean isAssociation();

	boolean isCollection();

}
