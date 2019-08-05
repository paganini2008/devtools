package com.allyes.components.amber.config;

import com.allyes.developer.utils.collection.Tuple;

/**
 * 
 * JobParameter
 * 
 * @author Fred Feng
 * @create 2018-03
 */
public interface JobParameter {

	String getName();

	Class<?> getJobClass();

	String getDescription();

	Tuple getTuple();

}