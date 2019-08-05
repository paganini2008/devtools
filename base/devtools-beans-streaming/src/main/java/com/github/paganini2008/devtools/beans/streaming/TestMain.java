package com.github.paganini2008.devtools.beans.streaming;

import com.github.paganini2008.devtools.beans.BeanUtils;
import com.github.paganini2008.devtools.converter.ConvertUtils;

public class TestMain {

	public static void main2(String[] args) {
		Boolean result =true;
		System.out.println(ConvertUtils.convertValue(result, boolean.class));

	}

	public static void main(String[] args) {
		Product product = new Product();
		product.setId(100);
		BeanUtils.setProperty(product, "id", 1000);
		BeanUtils.setProperty(product, "admin.username", "fengy");
		System.out.println(product.getId());
		System.out.println(product.getAdmin().getUsername());
		
		Product product2 = BeanUtils.copy(product);
		System.out.println(product2.getId());
		System.out.println(product2.getAdmin().getUsername());
		
	}
}
