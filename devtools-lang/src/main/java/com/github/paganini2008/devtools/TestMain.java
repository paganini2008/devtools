package com.github.paganini2008.devtools;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;

import com.github.paganini2008.devtools.comparator.ComparatorHelper;

public class TestMain {

	public static void main(String[] args) {
		Number a = 1L;
		Number b = 2d;
		System.out.println(((Comparable)a).compareTo((Comparable)b));

	}

}
