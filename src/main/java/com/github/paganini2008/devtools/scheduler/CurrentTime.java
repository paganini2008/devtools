package com.github.paganini2008.devtools.scheduler;

import java.util.Date;

/**
 * 
 * CurrentTime
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 * @version 1.0
 */
public interface CurrentTime {

	Date getTime();

	long getTimeInMillis();
}
