package com.github.paganini2008.devtools.date;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 日期时间计算对象
 * 
 * @author yan.feng
 * @version 1.0
 */
public class DateTime implements Serializable, Comparable<DateTime> {

	private static final long serialVersionUID = 6191643307288526696L;

	private final Calendar ref;

	public DateTime() {
		this(System.currentTimeMillis());
	}

	public DateTime(long time) {
		this(new Date(time));
	}

	public DateTime(Date date) {
		ref = Calendar.getInstance();
		ref.setLenient(false);
		ref.setTime(date);
	}

	protected DateTime(Calendar ref) {
		this.ref = ref;
	}

	public Date getTime() {
		return ref.getTime();
	}

	public long getTimeInMillis() {
		return ref.getTimeInMillis();
	}

	public boolean after(DateTime other) {
		return other.getTimeInMillis() < getTimeInMillis();
	}

	public boolean before(DateTime other) {
		return other.getTimeInMillis() > getTimeInMillis();
	}

	public boolean before(Date date) {
		return date.getTime() > getTimeInMillis();
	}

	public boolean after(Date date) {
		return date.getTime() < getTimeInMillis();
	}

	public String format(String pattern) {
		return DateUtils.format(getTime(), pattern);
	}

	private void reset(int calendarField) {
		switch (calendarField) {
		case Calendar.YEAR:
			setField(Calendar.MONTH, 0);
		case Calendar.MONTH:
			setField(Calendar.DAY_OF_MONTH, 0);
		case Calendar.DAY_OF_MONTH:
			setField(Calendar.HOUR_OF_DAY, 0);
		case Calendar.HOUR_OF_DAY:
			setField(Calendar.MINUTE, 0);
		case Calendar.MINUTE:
			setField(Calendar.SECOND, 0);
			break;
		default:
			break;
		}
	}

	public long delay() {
		return delay(System.currentTimeMillis());
	}

	public long delay(TimeUnit timeUnit) {
		return timeUnit.convert(delay(), TimeUnit.MILLISECONDS);
	}

	public long delay(long time) {
		return Math.abs(getTimeInMillis() - time);
	}

	public long delay(long time, TimeUnit timeUnit) {
		return timeUnit.convert(delay(time), TimeUnit.MILLISECONDS);
	}

	public DateTime addYears(int amount) {
		return addYears(amount, false);
	}

	public DateTime addYears(int amount, boolean reset) {
		addField(Calendar.YEAR, amount);
		if (reset) {
			reset(Calendar.YEAR);
		}
		return this;
	}

	public DateTime addMonths(int amount) {
		return addMonths(amount);
	}

	public DateTime addMonths(int amount, boolean reset) {
		addField(Calendar.MONTH, amount);
		if (reset) {
			reset(Calendar.MONTH);
		}
		return this;
	}

	public DateTime addDays(int amount) {
		return addDays(amount, false);
	}

	public DateTime addDays(int amount, boolean reset) {
		addField(Calendar.DAY_OF_MONTH, amount);
		if (reset) {
			reset(Calendar.DAY_OF_MONTH);
		}
		return this;
	}

	public DateTime addHours(int amount) {
		return addHours(amount, false);
	}

	public DateTime addHours(int amount, boolean reset) {
		addField(Calendar.HOUR_OF_DAY, amount);
		if (reset) {
			reset(Calendar.HOUR_OF_DAY);
		}
		return this;
	}

	public DateTime addMinutes(int amount) {
		return addMinutes(amount, false);
	}

	public DateTime addMinutes(int amount, boolean reset) {
		addField(Calendar.MINUTE, amount);
		if (reset) {
			reset(Calendar.MINUTE);
		}
		return this;
	}

	public DateTime addSeconds(int amount) {
		addField(Calendar.SECOND, amount);
		return this;
	}

	private void addField(int calendarField, int amount) {
		ref.add(calendarField, amount);
	}

	public DateTime setYears(int amount) {
		return setYears(amount, false);
	}

	public DateTime setYears(int amount, boolean reset) {
		setField(Calendar.YEAR, amount);
		if (reset) {
			reset(Calendar.YEAR);
		}
		return this;
	}

	public DateTime setMonths(int amount) {
		return setMonths(amount, false);
	}

	public DateTime setMonths(int amount, boolean reset) {
		setField(Calendar.MONTH, amount);
		if (reset) {
			reset(Calendar.MONTH);
		}
		return this;
	}

	public DateTime setDays(int amount) {
		return setDays(amount, false);
	}

	public DateTime setDays(int amount, boolean reset) {
		setField(Calendar.DAY_OF_MONTH, amount);
		if (reset) {
			reset(Calendar.DAY_OF_MONTH);
		}
		return this;
	}

	public DateTime setHours(int amount) {
		return setHours(amount, false);
	}

	public DateTime setHours(int amount, boolean reset) {
		setField(Calendar.HOUR_OF_DAY, amount);
		if (reset) {
			reset(Calendar.HOUR_OF_DAY);
		}
		return this;
	}

	public DateTime setMinutes(int amount) {
		return setMinutes(amount, false);
	}

	public DateTime setMinutes(int amount, boolean reset) {
		setField(Calendar.MINUTE, amount);
		if (reset) {
			reset(Calendar.MINUTE);
		}
		return this;
	}

	public DateTime setSeconds(int amount) {
		setField(Calendar.SECOND, amount);
		return this;
	}

	public int getYear() {
		return ref.get(Calendar.YEAR);
	}

	public int getMonth() {
		return ref.get(Calendar.MONTH);
	}

	public int getDay() {
		return ref.get(Calendar.DATE);
	}

	public int getHour() {
		return ref.get(Calendar.HOUR_OF_DAY);
	}

	public int getMinute() {
		return ref.get(Calendar.MINUTE);
	}

	public int getSecond() {
		return ref.get(Calendar.SECOND);
	}

	private void setField(int calendarField, int amount) {
		ref.set(calendarField, amount);
	}

	public String toString() {
		return ref.toString();
	}

	public DateTime copy() {
		return new DateTime((Calendar) ref.clone());
	}

	public int compareTo(DateTime other) {
		long l = getTimeInMillis() - other.getTimeInMillis();
		if (l > 0) {
			return 1;
		}
		if (l < 0) {
			return -1;
		}
		return 0;
	}

	public DateIterator ascendingIterator(Date endDate, int amount, Step step) {
		return new DateIterator(this.copy(), endDate, amount, step, true);
	}

	public DateIterator descendingIterator(Date endDate, int amount, Step step) {
		return new DateIterator(this.copy(), endDate, amount, step, false);
	}

}
