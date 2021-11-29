package com.github.paganini2008.devtools.beans.streaming.examples;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.github.paganini2008.devtools.Console;
import com.github.paganini2008.devtools.beans.BeanUtils;
import com.github.paganini2008.devtools.beans.MockContext;
import com.github.paganini2008.devtools.io.ImageUtils;

public class TestMain {

	public static void main(String[] args) throws Exception {
//		List<Product> list = BeanUtils.mockBeans(10000, Product.class, new MockContext());
//		Console.log(list);
		//Console.log(System.getProperties());
		System.out.println(ImageUtils.encode(new URL("http://asdfgh.wsy7.com/upload/2019/01/22/1237/1548131870672006141995.png"), "png"));
	}

}
