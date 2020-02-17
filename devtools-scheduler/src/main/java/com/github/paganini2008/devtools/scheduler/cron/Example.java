package com.github.paganini2008.devtools.scheduler.cron;

import com.github.paganini2008.devtools.date.DateUtils;

/**
 * 
 * Example
 *
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public class Example {

	public static void main(String[] args) {
		OneHour every = CronBuilder.thisYear().everyMonth(6, 8, 1).everyDay().pm();
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
