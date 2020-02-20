package com.github.paganini2008.devtools;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TestMain {

	public static void main(String[] args) {
		long ms = System.currentTimeMillis();
		Instant instant = Instant.ofEpochMilli(ms);
		System.out.println(instant.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

	}

}
