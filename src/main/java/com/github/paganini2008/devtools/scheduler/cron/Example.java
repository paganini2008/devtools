package com.github.paganini2008.devtools.scheduler.cron;

import com.github.paganini2008.devtools.date.DateUtils;

/**
 * 
 * Example
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 * @version 1.0
 */
public class Example {

	public static void main(String[] args) {
		Second every = Crons.everyYear().everyMonth(6, 11, 1).everyDay().everyHour().everyMinute(1).everySecond(3);
		int i=0;
		while(every.hasNext()) {
			String str = DateUtils.format(every.next().getTime());
			System.out.println(str); 
			if(i++>10) {
				break;
			}
		}
	}

}
