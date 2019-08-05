package com.github.paganini2008.devtools.net;

import java.net.URLEncoder;

public class TestMain {

	public static void main(String[] args) throws Exception{
		System.out.println(URLEncoder.encode("a=b&c=中国人&d= 1", "utf8"));
	}

}
