package com.github.paganini2008.devtools.objectpool;

/**
 * 
 * ObjectDetail
 * 
 * @author Fred Feng
 * @revised 2019-07
 * @created 2012-02
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