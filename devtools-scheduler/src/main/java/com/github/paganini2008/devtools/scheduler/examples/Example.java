package com.github.paganini2008.devtools.scheduler.examples;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.date.DateUtils;
import com.github.paganini2008.devtools.scheduler.Cancellable;
import com.github.paganini2008.devtools.scheduler.Cancellables;
import com.github.paganini2008.devtools.scheduler.CancellationException;
import com.github.paganini2008.devtools.scheduler.Task;
import com.github.paganini2008.devtools.scheduler.ThreadPoolTaskExecutor;
import com.github.paganini2008.devtools.scheduler.TimerTaskExecutor;
import com.github.paganini2008.devtools.scheduler.cron.CronBuilder;
import com.github.paganini2008.devtools.scheduler.cron.CronExpression;

/**
 * 
 * Example
 *
 * @author Fred Feng
 * @version 1.0
 */
public abstract class Example {

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

	public static void test1() throws Exception {
		getCron14().forEach(cron -> {
			System.out.println(DateUtils.format(cron.getTime()));
		});

		System.out.println(StringUtils.repeat("-", 32));

		getCron13().forEach(cron -> {
			System.out.println(DateUtils.format(cron.getTime()));
		});

		System.out.println(StringUtils.repeat("-", 32));
		getCron12().forEach(cron -> {
			System.out.println(DateUtils.format(cron.getTime()));
		});
	}

	public static void test2() throws Exception {
		CronBuilder.everySecond().test(new Task() {

			@Override
			public boolean execute() {
				System.out.println("Running at: " + DateUtils.format(System.currentTimeMillis()));
				return true;
			}

			@Override
			public Cancellable cancellable() {
				return Cancellables.cancelIfRuns(10);
			}

			public void onCancellation(Throwable e) {
				if (e instanceof CancellationException) {
					System.out.println(((CancellationException) e).getTaskDetail());
				} else {
					System.out.println("Cancelled.");
				}
			}

		});

	}

	public static void test3() throws Exception {
		CronExpression expression = CronBuilder.everySecond();
		TimerTaskExecutor executor = new TimerTaskExecutor();
		executor.schedule(new Task() {

			@Override
			public boolean execute() {
				System.out.println("Run at: " + DateUtils.format(System.currentTimeMillis()));
				return true;
			}

			@Override
			public Cancellable cancellable() {
				return Cancellables.cancelIfRuns(10);
			}

			@Override
			public void onCancellation(Throwable e) {
				System.out.println("Cancelled.");
			}

		}, expression);

		System.in.read();
		executor.close();
	}

	public static void test4() throws Exception {
		CronExpression expression = CronBuilder.everySecond();
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor(8, null);
		executor.schedule(new Task() {

			@Override
			public boolean execute() {
				System.out.println("Run at: " + DateUtils.format(System.currentTimeMillis()));
				return true;
			}

			@Override
			public Cancellable cancellable() {
				return Cancellables.cancelIfRuns(-1);
			}

			@Override
			public void onCancellation(Throwable e) {
				System.out.println("Cancelled.");
			}

		}, expression);

		System.in.read();
		executor.close();
	}

	public static void main(String[] args) throws Exception {
		test1();
	}

}
