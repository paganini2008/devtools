package com.github.paganini2008.devtools.scheduler;

import java.util.Calendar;

/**
 * 
 * ConcreteMonth
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 * @version 1.0
 */
public interface ConcreteMonth extends Month {

	ConcreteMonth andMonth(int andMonth);

	default ConcreteMonth andNextMonth() {
		return andNextMonths(1);
	}

	ConcreteMonth andNextMonths(int months);

	default ConcreteMonth toMonth(int andMonth) {
		return toMonth(andMonth, 1);
	}

	ConcreteMonth toMonth(int andMonth, int interval);

	default ConcreteMonth toMar() {
		return toMonth(Calendar.MARCH);
	}

	default ConcreteMonth toApr() {
		return toMonth(Calendar.APRIL);
	}

	default ConcreteMonth toMay() {
		return toMonth(Calendar.MAY);
	}

	default ConcreteMonth toJune() {
		return toMonth(Calendar.JUNE);
	}

	default ConcreteMonth toJuly() {
		return toMonth(Calendar.JULY);
	}

	default ConcreteMonth toAug() {
		return toMonth(Calendar.AUGUST);
	}

	default ConcreteMonth toSept() {
		return toMonth(Calendar.SEPTEMBER);
	}

	default ConcreteMonth toOct() {
		return toMonth(Calendar.OCTOBER);
	}

	default ConcreteMonth toNov() {
		return toMonth(Calendar.NOVEMBER);
	}

	default ConcreteMonth toDec() {
		return toMonth(Calendar.DECEMBER);
	}

	default ConcreteMonth andJan() {
		return andMonth(Calendar.JANUARY);
	}

	default ConcreteMonth andFeb() {
		return andMonth(Calendar.FEBRUARY);
	}

	default ConcreteMonth andMar() {
		return andMonth(Calendar.MARCH);
	}

	default ConcreteMonth andApr() {
		return andMonth(Calendar.APRIL);
	}

	default ConcreteMonth andMay() {
		return andMonth(Calendar.MAY);
	}

	default ConcreteMonth andJune() {
		return andMonth(Calendar.JUNE);
	}

	default ConcreteMonth andJuly() {
		return andMonth(Calendar.JULY);
	}

	default ConcreteMonth andAug() {
		return andMonth(Calendar.AUGUST);
	}

	default ConcreteMonth andSept() {
		return andMonth(Calendar.SEPTEMBER);
	}

	default ConcreteMonth andOct() {
		return andMonth(Calendar.OCTOBER);
	}

	default ConcreteMonth andNov() {
		return andMonth(Calendar.NOVEMBER);
	}

	default ConcreteMonth andDec() {
		return andMonth(Calendar.DECEMBER);
	}
}
