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
		Day every = CronBuilder.everyYear().everyMonth(6, 11, 1).everyDay();
		int i = 1;
		while (every.hasNext()) {
			String str = DateUtils.format(every.next().getTime());
			System.out.println(str);
			if (i++ >= 100) {
				break;
			}
		}
		System.out.println(i);
	}

}
