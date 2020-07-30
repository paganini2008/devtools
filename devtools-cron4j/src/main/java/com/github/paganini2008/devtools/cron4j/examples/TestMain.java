package com.github.paganini2008.devtools.cron4j.examples;

import java.util.Calendar;
import java.util.UUID;

import com.github.paganini2008.devtools.cron4j.cron.CronExpressionBuilder;
import com.github.paganini2008.devtools.cron4j.cron.CronExpression;
import com.github.paganini2008.devtools.date.DateUtils;

public class TestMain {

	public static void main(String[] args) {
		// System.out.println(CronBuilder.year(2020).Sept());

//		CronExpression time = CronExpressionBuilder.year(2020).Sept().toNov().dayOfWeek(3, Calendar.SATURDAY).and(2, Calendar.WEDNESDAY);
//		time.forEach(date -> {
//			System.out.println(DateUtils.format(date));
//		}, 10);
		System.out.println(UUID.randomUUID().toString().replace("-", ""));
	}

}
