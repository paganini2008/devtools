package com.github.paganini2008.devtools.beans.streaming.examples;

import java.util.List;

import com.github.paganini2008.devtools.Console;
import com.github.paganini2008.devtools.mock.BeanMocker;
import com.github.paganini2008.devtools.mock.MockContext;

public class TestMain {

	public static void main(String[] args) throws Exception {
		List<Product> list = BeanMocker.mockBeans(10000, Product.class, new MockContext());
		Console.log(list);
		//Console.log(System.getProperties());
		//System.out.println(ImageUtils.encode(new URL("http://asdfgh.wsy7.com/upload/2019/01/22/1237/1548131870672006141995.png"), "png"));
	}

}
