package com.github.paganini2008.springworld.cluster.implementor;

import java.util.List;

import com.github.paganini2008.devtools.RandomUtils;
import com.github.paganini2008.devtools.multithreads.AtomicPositiveInteger;

/**
 * 
 * LoadBalanceSelector
 *
 * @author Fred Feng
 * @created 2019-08
 * @revised 2019-08
 * @version 1.0
 */
public abstract class LoadBalanceSelector {

	public static class RoundrobinLoadBalance implements LoadBalance {

		private final AtomicPositiveInteger counter = new AtomicPositiveInteger();

		public String select(List<String> channels, Object message) {
			return channels.get(counter.getAndIncrement() % channels.size());
		}

	}

	public static class RandomLoadBalance implements LoadBalance {

		public String select(List<String> channels, Object message) {
			return channels.get(RandomUtils.randomInt(0, channels.size()));
		}

	}

}
