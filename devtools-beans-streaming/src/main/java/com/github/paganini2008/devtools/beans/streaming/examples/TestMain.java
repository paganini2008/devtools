package com.github.paganini2008.devtools.beans.streaming.examples;

import java.util.List;

import com.github.paganini2008.devtools.Console;
import com.github.paganini2008.devtools.beans.BeanUtils;
import com.github.paganini2008.devtools.beans.MockContext;

public class TestMain {

	public static void main(String[] args) {
		List<Product> list = BeanUtils.mockBeans(10000, Product.class, new MockContext());
		Console.log(list);
	}

}
