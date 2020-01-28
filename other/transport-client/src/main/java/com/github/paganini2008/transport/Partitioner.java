package com.github.paganini2008.transport;

import java.util.List;

/**
 * 
 * Partitioner
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
public interface Partitioner {

	<T> T selectChannel(Tuple tuple, List<T> channels);

}
