package com.github.paganini2008.devtools.beans.streaming.examples;

import java.util.List;

import com.github.paganini2008.devtools.Console;
import com.github.paganini2008.devtools.beans.BeanUtils;
import com.github.paganini2008.devtools.beans.MockConfig;

public class TestMain {

	public static void main(String[] args) {
		List<Product> list = BeanUtils.mockBeans(100, Product.class, new MockConfig() {

			@Override
			public boolean recurs(String propertyName, Class<?> propertyType) {
				return Product.Salesman.class == propertyType;
			}

		});
		Console.log(list);
	}

}
