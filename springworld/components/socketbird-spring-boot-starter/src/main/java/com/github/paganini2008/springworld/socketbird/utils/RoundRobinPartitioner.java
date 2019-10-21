package com.github.paganini2008.springworld.socketbird.utils;

import java.util.List;

import com.github.paganini2008.devtools.multithreads.AtomicPositiveLong;
import com.github.paganini2008.springworld.socketbird.Tuple;
import com.github.paganini2008.springworld.socketbird.transport.ChannelWrapper;

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

	public ChannelWrapper select(Tuple tuple, List<ChannelWrapper> channels) {
		try {
			int index = (int) (sequence.getAndIncrement() % channels.size());
			return channels.get(index);
		} catch (RuntimeException e) {
			return null;
		}
	}

}
