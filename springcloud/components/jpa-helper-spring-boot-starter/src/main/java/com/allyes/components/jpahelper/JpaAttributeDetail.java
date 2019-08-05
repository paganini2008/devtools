package com.allyes.components.jpahelper;

/**
 * 
 * JpaAttributeDetail
 * 
 * @author Fred Feng
 * @created 2019-02
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
