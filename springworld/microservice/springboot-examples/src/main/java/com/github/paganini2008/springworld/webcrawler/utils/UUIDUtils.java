package com.github.paganini2008.springworld.webcrawler.utils;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 
 * UUIDUtils
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
public abstract class UUIDUtils {

	private static final long leastSigBits;
	private static AtomicLong lastTime = new AtomicLong();

	static {
		byte[] seed = new SecureRandom().generateSeed(8);
		leastSigBits = new BigInteger(seed).longValue();
	}

	public static UUID createTimeBasedUUID() {
		long timeMillis = (System.currentTimeMillis() * 10000) + 0x01B21DD213814000L;

		if (timeMillis > lastTime.get()) {
			lastTime.getAndSet(timeMillis);
		} else {
			timeMillis = lastTime.incrementAndGet();
		}

		long mostSigBits = timeMillis << 32;
		mostSigBits |= (timeMillis & 0xFFFF00000000L) >> 16;
		mostSigBits |= 0x1000 | ((timeMillis >> 48) & 0x0FFF);

		return new UUID(mostSigBits, leastSigBits);
	}

	public static void main(String[] args) throws Exception {
		ExecutorService executorService = Executors.newFixedThreadPool(16);
		for (int i = 0; i < 10000; i++) {
			executorService.execute(() -> {
				System.out.println(UUIDUtils.createTimeBasedUUID().timestamp());
			});

		}
		System.in.read();
		executorService.shutdown();
	}
}
