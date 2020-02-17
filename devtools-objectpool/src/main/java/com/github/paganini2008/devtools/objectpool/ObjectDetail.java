package com.github.paganini2008.devtools.objectpool;

/**
 * 
 * ObjectDetail
 * 
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public interface ObjectDetail {

	long getCreated();

	Object getObject();

	long getLastBorrowed();

	long getLastReturned();

	long getLastTested();

	int getUses();

}