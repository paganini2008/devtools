package com.github.paganini2008.devtools.scheduler.cron;

import java.util.Calendar;

/**
 * 
 * OneMonth
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 * @version 1.0
 */
public interface OneMonth extends Month {

	OneMonth andMonth(int andMonth);

	default OneMonth andNextMonth() {
		return andNextMonths(1);
	}

	OneMonth andNextMonths(int months);

	default OneMonth toMonth(int andMonth) {
		return toMonth(andMonth, 1);
	}

	OneMonth toMonth(int andMonth, int interval);

	default OneMonth toMar() {
		return toMonth(Calendar.MARCH);
	}

	default OneMonth toApr() {
		return toMonth(Calendar.APRIL);
	}

	default OneMonth toMay() {
		return toMonth(Calendar.MAY);
	}

	default OneMonth toJune() {
		return toMonth(Calendar.JUNE);
	}

	default OneMonth toJuly() {
		return toMonth(Calendar.JULY);
	}

	default OneMonth toAug() {
		return toMonth(Calendar.AUGUST);
	}

	default OneMonth toSept() {
		return toMonth(Calendar.SEPTEMBER);
	}

	default OneMonth toOct() {
		return toMonth(Calendar.OCTOBER);
	}

	default OneMonth toNov() {
		return toMonth(Calendar.NOVEMBER);
	}

	default OneMonth toDec() {
		return toMonth(Calendar.DECEMBER);
	}

	default OneMonth andJan() {
		return andMonth(Calendar.JANUARY);
	}

	default OneMonth andFeb() {
		return andMonth(Calendar.FEBRUARY);
	}

	default OneMonth andMar() {
		return andMonth(Calendar.MARCH);
	}

	default OneMonth andApr() {
		return andMonth(Calendar.APRIL);
	}

	default OneMonth andMay() {
		return andMonth(Calendar.MAY);
	}

	default OneMonth andJune() {
		return andMonth(Calendar.JUNE);
	}

	default OneMonth andJuly() {
		return andMonth(Calendar.JULY);
	}

	default OneMonth andAug() {
		return andMonth(Calendar.AUGUST);
	}

	default OneMonth andSept() {
		return andMonth(Calendar.SEPTEMBER);
	}

	default OneMonth andOct() {
		return andMonth(Calendar.OCTOBER);
	}

	default OneMonth andNov() {
		return andMonth(Calendar.NOVEMBER);
	}

	default OneMonth andDec() {
		return andMonth(Calendar.DECEMBER);
	}
}
