package com.github.paganini2008.devtools.cron4j.examples;

import com.github.paganini2008.devtools.cron4j.cron.CronBuilder;
import com.github.paganini2008.devtools.cron4j.cron.CronExpression;
import com.github.paganini2008.devtools.cron4j.cron.Day;
import com.github.paganini2008.devtools.cron4j.cron.Week;
import com.github.paganini2008.devtools.date.DateUtils;

public class TestMain {

	public static void main(String[] args) {
		//System.out.println(CronBuilder.year(2020).Sept());
		
		Week week = CronBuilder.year(2020).Sept().week(1);
		week.forEach(date->{
			System.out.println(DateUtils.format(date));
		}, 10);
	}

}
