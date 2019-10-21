package com.github.paganini2008.springworld.socketbird.utils;

import java.util.List;

import com.github.paganini2008.springworld.socketbird.Tuple;
import com.github.paganini2008.springworld.socketbird.transport.ChannelWrapper;

/**
 * 
 * Partitioner
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
public interface Partitioner {

	ChannelWrapper select(Tuple tuple, List<ChannelWrapper> channels);

}
