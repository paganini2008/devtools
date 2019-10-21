package com.github.paganini2008.springworld.socketbird.utils;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.github.paganini2008.springworld.socketbird.Tuple;
import com.github.paganini2008.springworld.socketbird.transport.ChannelWrapper;

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

	public ChannelWrapper select(Tuple tuple, List<ChannelWrapper> channels) {
		try {
			return channels.get(ThreadLocalRandom.current().nextInt(channels.size()));
		} catch (RuntimeException e) {
			return null;
		}
	}

}
