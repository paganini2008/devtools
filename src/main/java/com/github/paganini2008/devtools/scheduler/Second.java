package com.github.paganini2008.devtools.scheduler;

import java.util.Iterator;

/**
 * 
 * Second
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 * @version 1.0
 */
public interface Second extends Iterator<Second>, CurrentTime {

	int getYear();

	int getMonth();

	int getDay();

	int getHour();

	int getMinute();

	int getSecond();

}
