package com.github.paganini2008.springworld.cluster.multicast;

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

	String select(Object message, List<String> channels);

}
