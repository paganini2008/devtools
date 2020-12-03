package com.github.paganini2008.devtools.cron4j.cron;

/**
 * 
 * TheSecond
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface TheSecond extends Second {

	TheSecond andSecond(int second);

	default TheSecond toSecond(int second) {
		return toSecond(second, 1);
	}

	TheSecond toSecond(int second, int interval);

}
