package com.github.paganini2008.devtools.scheduler;

import java.util.Calendar;

public class Unit {

	private final int calenderField;
	private final int amount;

	public Unit(int calenderField, int amount) {
		this.calenderField = calenderField;
		this.amount = amount;
	}

	public Calendar getTime(Calendar calendar) {
		Calendar copy = (Calendar) calendar.clone();
		copy.set(calenderField, amount);
		switch (calenderField) {
		case Calendar.YEAR:
			copy.set(Calendar.MONTH, Calendar.JANUARY);
		case Calendar.MONTH:
			copy.set(Calendar.DAY_OF_MONTH, 1);
		case Calendar.DAY_OF_MONTH:
			copy.set(Calendar.HOUR_OF_DAY, 0);
		case Calendar.HOUR:
			copy.set(Calendar.MINUTE, 0);
		case Calendar.MINUTE:
			copy.set(Calendar.SECOND, 0);
			break;
		}
		return copy;
	}
	
	public static void main(String[] args) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, 5);
		System.out.println(cal.getActualMaximum(Calendar.WEEK_OF_MONTH));
	}

}
