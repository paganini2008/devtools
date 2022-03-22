package com.github.paganini2008.devtools;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class TestMain {

	public static void main(String[] args) {
		LocalDateTime ld = LocalDateTime.now();
		ld = ld.plus(1200, ChronoUnit.SECONDS);
		System.out.println(ld);
	}

}
