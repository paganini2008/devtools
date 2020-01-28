package com.github.paganini2008.transport;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 
 * RandomPartitioner
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
public class RandomPartitioner implements Partitioner {

	public <T> T selectChannel(Tuple tuple, List<T> channels) {
		try {
			return channels.get(ThreadLocalRandom.current().nextInt(channels.size()));
		} catch (RuntimeException e) {
			return null;
		}
	}

}
