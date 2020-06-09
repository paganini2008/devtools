package com.github.paganini2008.devtools.cron4j.cron;

import java.util.Calendar;

/**
 * 
 * ThatMonth
 *
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public interface ThatMonth extends Month {

	ThatMonth andMonth(int andMonth);

	default ThatMonth toMonth(int andMonth) {
		return toMonth(andMonth, 1);
	}

	ThatMonth toMonth(int andMonth, int interval);

	default ThatMonth toMar() {
		return toMonth(Calendar.MARCH);
	}

	default ThatMonth toApr() {
		return toMonth(Calendar.APRIL);
	}

	default ThatMonth toMay() {
		return toMonth(Calendar.MAY);
	}

	default ThatMonth toJune() {
		return toMonth(Calendar.JUNE);
	}

	default ThatMonth toJuly() {
		return toMonth(Calendar.JULY);
	}

	default ThatMonth toAug() {
		return toMonth(Calendar.AUGUST);
	}

	default ThatMonth toSept() {
		return toMonth(Calendar.SEPTEMBER);
	}

	default ThatMonth toOct() {
		return toMonth(Calendar.OCTOBER);
	}

	default ThatMonth toNov() {
		return toMonth(Calendar.NOVEMBER);
	}

	default ThatMonth toDec() {
		return toMonth(Calendar.DECEMBER);
	}

	default ThatMonth andJan() {
		return andMonth(Calendar.JANUARY);
	}

	default ThatMonth andFeb() {
		return andMonth(Calendar.FEBRUARY);
	}

	default ThatMonth andMar() {
		return andMonth(Calendar.MARCH);
	}

	default ThatMonth andApr() {
		return andMonth(Calendar.APRIL);
	}

	default ThatMonth andMay() {
		return andMonth(Calendar.MAY);
	}

	default ThatMonth andJune() {
		return andMonth(Calendar.JUNE);
	}

	default ThatMonth andJuly() {
		return andMonth(Calendar.JULY);
	}

	default ThatMonth andAug() {
		return andMonth(Calendar.AUGUST);
	}

	default ThatMonth andSept() {
		return andMonth(Calendar.SEPTEMBER);
	}

	default ThatMonth andOct() {
		return andMonth(Calendar.OCTOBER);
	}

	default ThatMonth andNov() {
		return andMonth(Calendar.NOVEMBER);
	}

	default ThatMonth andDec() {
		return andMonth(Calendar.DECEMBER);
	}
}
