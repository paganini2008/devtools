package com.github.paganini2008.devtools.http;

public class TestHttp {

	public static void main(String[] args)throws Exception{
		String url = "http://localhost:9200/lazycat-ccmsui/auth.htm";
		Connection c = Connection.post(url);
		c.validateTLSCertificates(false);
		c.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.835.163 Safari/535.1");
		c.timeout(60 * 1000);
		c.ignoreContentType(true);
		c.data("username", "admin");
		c.data("password", "admin123");
		c.data("role", "admin");
		c.charset("utf-8");
		HttpResponse response = c.execute();
		System.out.println(response);
		System.out.println(response.body());
		System.out.println(c.elapsedTime());
		System.out.println("TeatMain.main()");
	}
	
}
