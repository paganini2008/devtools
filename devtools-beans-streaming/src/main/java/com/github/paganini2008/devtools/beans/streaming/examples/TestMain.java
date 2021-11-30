package com.github.paganini2008.devtools.beans.streaming.examples;

import java.util.List;

import com.github.paganini2008.devtools.Console;
import com.github.paganini2008.devtools.mock.BeanMocker;
import com.github.paganini2008.devtools.mock.MockContext;

public class TestMain {

	public static void main(String[] args) throws Exception {
		List<Product> list = BeanMocker.mockBeans(10000, Product.class, new MockContext());
		Console.log(list);
	}

}
