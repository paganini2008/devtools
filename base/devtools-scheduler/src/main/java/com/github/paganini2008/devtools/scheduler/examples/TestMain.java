package com.github.paganini2008.devtools.scheduler.examples;

import com.github.paganini2008.devtools.date.DateUtils;
import com.github.paganini2008.devtools.io.SerializationUtils;
import com.github.paganini2008.devtools.scheduler.cron.CronBuilder;
import com.github.paganini2008.devtools.scheduler.cron.Minute;
import com.github.paganini2008.devtools.scheduler.cron.Second;

public class TestMain {

	public static void main(String[] args) {
		Minute minute = CronBuilder.everyDay().everyHour(3).everyMinute(10);
		Minute minute2 = SerializationUtils.clone(minute);
		System.out.println(minute == minute2);
		int i = 0;
		while (minute2.hasNext()) {
			minute2 = minute2.next();
			System.out.println(DateUtils.format(minute2.getTime()));
			if (i++ >= 20) {
				break;
			}
		}

	}

}
