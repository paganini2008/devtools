package com.github.paganini2008.devtools.scheduler;

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
		Day weekDay = Crons.thisMonth().everyDay();
		while(weekDay.hasNext()) {
			String str = DateUtils.format(weekDay.getTime());
			System.out.println(str); 
		}
	}

}
