package com.github.paganini2008.devtools.multithreads;

public class TestMain {

	public static void main(String[] args) {
		final AtomicPositiveInteger counter = new AtomicPositiveInteger(0);
		for(int i=0;i<100;i++) {
			System.out.println(counter.getAndIncrement() % 8);
		}

	}

}
