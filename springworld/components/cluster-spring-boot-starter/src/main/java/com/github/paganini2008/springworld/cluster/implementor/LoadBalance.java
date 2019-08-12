package com.github.paganini2008.springworld.cluster.implementor;

import java.util.List;

/**
 * 
 * LoadBalance
 *
 * @author Fred Feng
 * @revised 2019-08
 * @created 2019-08
 * @version 1.0
 */
public interface LoadBalance {

	String select(List<String> channels, Object message);

}
