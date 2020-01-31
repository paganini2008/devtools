package com.github.paganini2008.springworld.cluster.multicast;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.github.paganini2008.devtools.multithreads.AtomicUnsignedInteger;

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

		private final AtomicUnsignedInteger counter = new AtomicUnsignedInteger();

		public String select(Object message, List<String> channels) {
			return channels.get(counter.getAndIncrement() % channels.size());
		}

	}

	public static class RandomLoadBalance implements LoadBalance {

		public String select(Object message, List<String> channels) {
			return channels.get(ThreadLocalRandom.current().nextInt(0, channels.size()));
		}

	}

	public static class HashLoadBalance implements LoadBalance {

		public String select(Object message, List<String> channels) {
			int hash = message.hashCode();
			hash &= 0x7FFFFFFF;
			return channels.get(hash % channels.size());
		}

	}

}
