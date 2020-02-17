package com.github.paganini2008.devtools.scheduler.examples;

import com.github.paganini2008.devtools.date.DateUtils;
import com.github.paganini2008.devtools.scheduler.cron.CronBuilder;
import com.github.paganini2008.devtools.scheduler.cron.CronExpression;

/**
 * 
 * Example
 *
 * @author Fred Feng
 * @version 1.0
 */
public class Example {

	// */5 * * * * ?
	public static CronExpression getCron1() {
		return CronBuilder.everySecond(5);
	}

	// 0 */2 * * * ?
	public static CronExpression getCron2() {
		return CronBuilder.everyMinute(2);
	}

	// 0 26,29,33 * * * ?
	public static CronExpression getCron3() {
		return CronBuilder.minute(26).andMinute(29).andMinute(33);
	}

	// 0 * 14 * * ?
	public static CronExpression getCron4() {
		return CronBuilder.hour(14).everyMinute(1);
	}

	// 0 0-10 15 * * ?
	public static CronExpression getCron5() {
		return CronBuilder.hour(15).minute(0).toMinute(10);
	}

	// 0 0 23 * * ?
	public static CronExpression getCron6() {
		return CronBuilder.everyDay().hour(23);
	}

	// 0 15 12 * * ?
	public static CronExpression getCron7() {
		return CronBuilder.everyDay().hour(12).minute(15);
	}

	// 0 0 0,13,18,21 * * ?
	public static CronExpression getCron8() {
		return CronBuilder.hour(13).andHour(18).andHour(21);
	}

	// 0 15 10 ? * 6L
	public static CronExpression getCron9() {
		return CronBuilder.everyMonth().lastWeek().Fri().at(10, 15);
	}

	// 0 15 10 ? * MON-FRI
	public static CronExpression getCron10() {
		return CronBuilder.everyWeek().Mon().toFri().at(10, 15, 0);
	}

	// 0 0/5 12,18 * * ?
	public static CronExpression getCron11() {
		return CronBuilder.hour(12).toHour(18).everyMinute(5);
	}

	// 0 30 23 L * ?
	public static CronExpression getCron12() {
		return CronBuilder.everyMonth().lastDay().at(23, 30);
	}

	// 0 10,20,30 12 ? 3,4 5L 2002-2005
	public static CronExpression getCron13() {
		return CronBuilder.year(2020).toYear(2025).Mar().andApr().lastWeek().Thur().hour(12).minute(10).andMinute(20).andMinute(30);
	}

	// 0 10 23 ? * 6#3
	public static CronExpression getCron14() {
		return CronBuilder.everyMonth().week(3).Fri().at(23, 10);
	}

	public static void main(String[] args) {
		// getCron14().forEach(cron -> {
		// System.out.println(DateUtils.format(cron.getTime()));
		// });

		getCron13().forEach(cron -> {
			System.out.println(DateUtils.format(cron.getTime()));
		});

	}

}
