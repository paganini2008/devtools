package com.github.paganini2008.springworld.support;

import java.util.Arrays;

import com.github.paganini2008.devtools.Cases;
import com.github.paganini2008.devtools.beans.TupleImpl;
import com.github.paganini2008.devtools.collection.Tuple;

public class TestMain {

	public static void main(String[] args) {
		Tuple tuple = new TupleImpl(Cases.UNDER_SCORE);
		tuple.set("abc", 1);
		tuple.set("abc_fun", 2);
		tuple.set("abc_test", 3);
		System.out.println(tuple.get("abcTest"));
	}

}
