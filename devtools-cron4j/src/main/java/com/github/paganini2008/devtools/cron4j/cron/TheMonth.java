package com.github.paganini2008.devtools.cron4j.cron;

import java.util.Calendar;

/**
 * 
 * TheMonth
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface TheMonth extends Month {

	TheMonth andMonth(int andMonth);

	default TheMonth toMonth(int andMonth) {
		return toMonth(andMonth, 1);
	}

	TheMonth toMonth(int andMonth, int interval);

	default TheMonth toMar() {
		return toMonth(Calendar.MARCH);
	}

	default TheMonth toApr() {
		return toMonth(Calendar.APRIL);
	}

	default TheMonth toMay() {
		return toMonth(Calendar.MAY);
	}

	default TheMonth toJune() {
		return toMonth(Calendar.JUNE);
	}

	default TheMonth toJuly() {
		return toMonth(Calendar.JULY);
	}

	default TheMonth toAug() {
		return toMonth(Calendar.AUGUST);
	}

	default TheMonth toSept() {
		return toMonth(Calendar.SEPTEMBER);
	}

	default TheMonth toOct() {
		return toMonth(Calendar.OCTOBER);
	}

	default TheMonth toNov() {
		return toMonth(Calendar.NOVEMBER);
	}

	default TheMonth toDec() {
		return toMonth(Calendar.DECEMBER);
	}

	default TheMonth andJan() {
		return andMonth(Calendar.JANUARY);
	}

	default TheMonth andFeb() {
		return andMonth(Calendar.FEBRUARY);
	}

	default TheMonth andMar() {
		return andMonth(Calendar.MARCH);
	}

	default TheMonth andApr() {
		return andMonth(Calendar.APRIL);
	}

	default TheMonth andMay() {
		return andMonth(Calendar.MAY);
	}

	default TheMonth andJune() {
		return andMonth(Calendar.JUNE);
	}

	default TheMonth andJuly() {
		return andMonth(Calendar.JULY);
	}

	default TheMonth andAug() {
		return andMonth(Calendar.AUGUST);
	}

	default TheMonth andSept() {
		return andMonth(Calendar.SEPTEMBER);
	}

	default TheMonth andOct() {
		return andMonth(Calendar.OCTOBER);
	}

	default TheMonth andNov() {
		return andMonth(Calendar.NOVEMBER);
	}

	default TheMonth andDec() {
		return andMonth(Calendar.DECEMBER);
	}
}
