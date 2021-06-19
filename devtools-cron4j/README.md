# devtools-cron4j

<code>devtools-cron4j</code> is a small and practical Java scheduling toolkit from the [devtools](https://github.com/paganini2008/devtools.git) series, which provides:

1. API-oriented way to customize <code>cron</code> expressions, and can parse <code>cron</code> expressions into the form of API
2. Built-in multiple schedulers can execute target content regularly
3. Without relying on other components, you can customize your own system lightly

###  Compatibility
1. JDK 1.8 (or later)

### Install
``` xml
<dependency>
	<groupId>com.github.paganini2008</groupId>
	<artifactId>devtools-cron4j</artifactId>
	<version>2.0.1</version>
</dependency>
```

### How to generate cron expression?
``` java
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
		return CronExpressionBuilder.everyHour().minute(26).andMinute(29).andMinute(33);
	}

	// 0 * 14 * * ?
	public static CronExpression getCron4() {
		return CronExpressionBuilder.everyDay().hour(14).everyMinute();
	}

	// 0 0-10 15 * * ?
	public static CronExpression getCron5() {
		return CronExpressionBuilder.everyDay().hour(15).minute(0).toMinute(10);
	}

	// 0 0 23 * * ?
	public static CronExpression getCron6() {
		return CronExpressionBuilder.everyDay().at(23, 0);
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
		return CronExpressionBuilder.hour(12).andHour(18).everyMinute(5);
	}

	// 0 30 23 L * ?
	public static CronExpression getCron12() {
		return CronExpressionBuilder.everyMonth().lastDay().at(23, 30);
	}

	// 0 10,20,30 12 ? 7-11 6L 2021-2025
	public static CronExpression getCron13() {
		return CronExpressionBuilder.year(2021).toYear(2025).Aug().toDec().lastWeek().Fri().hour(12).minute(10).andMinute(20).andMinute(30);
	}

	// 0 10 23 ? * 6#3
	public static CronExpression getCron14() {
		return CronExpressionBuilder.everyMonth().week(3).Fri().at(23, 10);
	}

	// 0 15-50/2 0-6 10-28 * ?
	public static CronExpression getCron15() {
		return CronExpressionBuilder.everyMonth().day(10).toDay(28).hour(0).toHour(6).minute(15).toMinute(50, 2);
	}
```

### How to parse cron expressions?
``` java
        System.out.println(CRON.parse("*/5 * * * * ?"));
		System.out.println(CRON.parse("0 */2 * * * ?"));
		System.out.println(CRON.parse("0 15 10 LW * ?"));
		System.out.println(CRON.parse("0 0 12 10W * ?"));
		System.out.println(CRON.parse("0 15 10 ? * MON-FRI"));
		System.out.println(CRON.parse("0 26,29,33 * * * ?"));
		System.out.println(CRON.parse("0 15-50/2 0-6 10-28 * ?"));
		System.out.println(CRON.parse("0 10 23 ? * 6#3"));
		System.out.println(CRON.parse("0 10,20,30 12 ? 7-11 6L 2021-2025"));
```

### How to test cron expressions?
``` java
		CRON.parse("0 30 23 L * ?").forEach(date -> {
			System.out.println(DateUtils.format(date));
		}, 20);

		System.out.println("-----------------------------------------");

		CRON.parse("0 0 12 10-15 * ?").forEach(date -> {
			System.out.println(DateUtils.format(date));
		}, 20);
```
### How to run the scheduler?
``` java
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
```
