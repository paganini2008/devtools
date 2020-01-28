package com.github.paganini2008.transport;

import java.util.List;

import com.github.paganini2008.devtools.multithreads.AtomicPositiveLong;

/**
 * 
 * RoundRobinPartitioner
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
public class RoundRobinPartitioner implements Partitioner {

	private final AtomicPositiveLong sequence = new AtomicPositiveLong();

	public <T> T selectChannel(Tuple tuple, List<T> channels) {
		try {
			int index = (int) (sequence.getAndIncrement() % channels.size());
			return channels.get(index);
		} catch (RuntimeException e) {
			return null;
		}
	}

}
