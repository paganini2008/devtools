package com.github.paganini2008.devtools.scheduler.cron;

/**
 * 
 * OneSecond
 *
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public interface OneSecond extends Second {

	OneSecond andSecond(int second);

	default OneSecond toSecond(int second) {
		return toSecond(second, 1);
	}

	OneSecond toSecond(int second, int interval);

}
