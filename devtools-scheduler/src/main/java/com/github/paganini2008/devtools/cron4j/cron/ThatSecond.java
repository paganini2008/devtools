package com.github.paganini2008.devtools.cron4j.cron;

/**
 * 
 * ThatSecond
 *
 * @author Fred Feng
 * @version 1.0
 */
public interface ThatSecond extends Second {

	ThatSecond andSecond(int second);

	default ThatSecond toSecond(int second) {
		return toSecond(second, 1);
	}

	ThatSecond toSecond(int second, int interval);

}
