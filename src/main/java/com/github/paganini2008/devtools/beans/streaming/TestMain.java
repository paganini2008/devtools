package com.github.paganini2008.devtools.beans.streaming;

import com.github.paganini2008.devtools.converter.ConvertUtils;

public class TestMain {

	public static void main(String[] args) {
		Boolean result =true;
		System.out.println(ConvertUtils.convertValue(result, boolean.class));

	}

}
