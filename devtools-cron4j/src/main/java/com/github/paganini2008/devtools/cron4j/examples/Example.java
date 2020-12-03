package com.github.paganini2008.devtools.cron4j.examples;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.cron4j.Cancellable;
import com.github.paganini2008.devtools.cron4j.Cancellables;
import com.github.paganini2008.devtools.cron4j.CancellationException;
import com.github.paganini2008.devtools.cron4j.ClockTaskExecutor;
import com.github.paganini2008.devtools.cron4j.Task;
import com.github.paganini2008.devtools.cron4j.TaskExecutor.TaskFuture;
import com.github.paganini2008.devtools.cron4j.ThreadPoolTaskExecutor;
import com.github.paganini2008.devtools.cron4j.TimerTaskExecutor;
import com.github.paganini2008.devtools.cron4j.cron.CronExpressionBuilder;
import com.github.paganini2008.devtools.cron4j.cron.CronExpression;
import com.github.paganini2008.devtools.date.DateUtils;

/**
 * 
 * Example
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public abstract class Example {

	// */5 * * * * ?
	public static CronExpression getCron1() {
		return CronExpressionBuilder.everySecond(5);
	}

	// 0 */2 * * * ?
	public static CronExpression getCron2() {
		return CronExpressionBuilder.everyMinute(2);
	}

	// 0 26,29,33 * * * ?
	public static CronExpression getCron3() {
		return CronExpressionBuilder.minute(26).andMinute(29).andMinute(33);
	}

	// 0 * 14 * * ?
	public static CronExpression getCron4() {
		return CronExpressionBuilder.hour(14).everyMinute(1);
	}

	// 0 0-10 15 * * ?
	public static CronExpression getCron5() {
		return CronExpressionBuilder.hour(15).minute(0).toMinute(10);
	}

	// 0 0 23 * * ?
	public static CronExpression getCron6() {
		return CronExpressionBuilder.everyDay().hour(23);
	}

	// 0 15 12 * * ?
	public static CronExpression getCron7() {
		return CronExpressionBuilder.everyDay().hour(12).minute(15);
	}

	// 0 0 0,13,18,21 * * ?
	public static CronExpression getCron8() {
		return CronExpressionBuilder.hour(13).andHour(18).andHour(21);
	}

	// 0 15 10 ? * 6L
	public static CronExpression getCron9() {
		return CronExpressionBuilder.everyMonth().lastWeek().Fri().at(10, 15);
	}

	// 0 15 10 ? * MON-FRI
	public static CronExpression getCron10() {
		return CronExpressionBuilder.everyWeek().Mon().toFri().at(10, 15, 0);
	}

	// 0 0/5 12,18 * * ?
	public static CronExpression getCron11() {
		return CronExpressionBuilder.hour(12).toHour(18).everyMinute(5);
	}

	// 0 30 23 L * ?
	public static CronExpression getCron12() {
		return CronExpressionBuilder.everyMonth().lastDay().at(23, 30);
	}

	// 0 10,20,30 12 ? 3,4 5L 2020-2025
	public static CronExpression getCron13() {
		return CronExpressionBuilder.year(2020).Aug().toDec().lastWeek().Fri().hour(12).minute(10).andMinute(20).andMinute(30);
	}

	// 0 10 23 ? * 6#3
	public static CronExpression getCron14() {
		return CronExpressionBuilder.everyMonth().week(3).Fri().at(23, 10);
	}

	public static void test1() throws Exception {
		// getCron14().forEach(date -> {
		// System.out.println(DateUtils.format(date));
		// }, 10);

		// System.out.println(StringUtils.repeat("-", 32));
		//
		getCron13().forEach(date -> {
			System.out.println(DateUtils.format(date));
		}, 20);

		// System.out.println(StringUtils.repeat("-", 32));
		// getCron10().forEach(date -> {
		// System.out.println(DateUtils.format(date));
		// }, 20);
	}

	public static void test2() throws Exception {
		final TaskFuture taskFuture = CronExpressionBuilder.everySecond().test(new Task() {

			@Override
			public boolean execute() {
				System.out.println("Running at: " + DateUtils.format(System.currentTimeMillis()) + ", Next fire: ");
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
		CronExpression expression = CronExpressionBuilder.everySecond();
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
		CronExpression expression = CronExpressionBuilder.everySecond();
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
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

	public static void test5() throws Exception {
		CronExpression expression = CronExpressionBuilder.everySecond(5);
		ClockTaskExecutor executor = new ClockTaskExecutor();
		executor.schedule(new Task() {

			@Override
			public boolean execute() {
				System.out.println("Run at: " + DateUtils.format(System.currentTimeMillis()) + ", next: "
						+ DateUtils.format(executor.getTaskFuture(this).getDetail().nextExectionTime()));
				return true;
			}

			@Override
			public Cancellable cancellable() {
				return Cancellables.cancelIfRuns(100);
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
		test5();
	}

}
