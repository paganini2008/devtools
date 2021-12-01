/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.github.paganini2008.devtools.cron4j.examples;

import com.github.paganini2008.devtools.cron4j.CRON;
import com.github.paganini2008.devtools.cron4j.Cancellable;
import com.github.paganini2008.devtools.cron4j.Cancellables;
import com.github.paganini2008.devtools.cron4j.CancellationException;
import com.github.paganini2008.devtools.cron4j.ClockTaskExecutor;
import com.github.paganini2008.devtools.cron4j.Task;
import com.github.paganini2008.devtools.cron4j.TaskExecutor.TaskFuture;
import com.github.paganini2008.devtools.cron4j.ThreadPoolTaskExecutor;
import com.github.paganini2008.devtools.cron4j.TimerTaskExecutor;
import com.github.paganini2008.devtools.cron4j.cron.CronExpression;
import com.github.paganini2008.devtools.cron4j.cron.CronExpressionBuilder;
import com.github.paganini2008.devtools.time.DateUtils;

/**
 * 
 * Example2
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
public class Example2 {

	public static void test1() throws Exception {
		CRON.parse("0 30 23 L * ?").forEach(date -> {
			System.out.println(DateUtils.format(date));
		}, 20);

		System.out.println("-----------------------------------------");

		CRON.parse("0 0 12 10-15 * ?").forEach(date -> {
			System.out.println(DateUtils.format(date));
		}, 20);

		System.out.println("-----------------------------------------");
		CRON.parse("0 0 12 31W * ?").forEach(date -> {
			System.out.println(DateUtils.format(date));
		}, 10);
	}

	public static void test2() throws Exception {
		final TaskFuture taskFuture = CronExpressionBuilder.everySecond().test(new Task() {

			@Override
			public boolean execute() {
				System.out.println("Running at: " + DateUtils.format(System.currentTimeMillis()));
				return true;
			}

			@Override
			public Cancellable cancellable() {
				return Cancellables.cancelIfRuns(10);
			}

			@Override
			public void onCancellation(Throwable e) {
				if (e instanceof CancellationException) {
					System.out.println(((CancellationException) e).getTaskDetail());
				} else {
					System.out.println("Cancelled.");
				}
			}

		});
		System.out.println(taskFuture);

	}

	public static void test3() throws Exception {
		CronExpression expression = CronExpressionBuilder.everySecond(5);
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
		CronExpression expression = CronExpressionBuilder.everySecond(5);
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
		test1();

	}

}
