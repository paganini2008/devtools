package com.github.paganini2008.devtools.http;

import java.io.File;
import java.util.Map;

import com.github.paganini2008.devtools.io.FileUtils;

/**
 * Requests
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class Requests {

	private Requests() {
	}

	public static RequestBuilder2 get(String url) {
		RequestBuilder2 rb = new Connection(url);
		rb.method(HttpMethod.GET);
		return rb;
	}

	public static RequestBuilder2 get(String url, Map<String, String> headers, Map<String, String> data) {
		RequestBuilder2 rb = get(url);
		rb.headers(headers);
		rb.data(data);
		return rb;
	}

	public static RequestBuilder2 post(String url) {
		RequestBuilder2 rb = new Connection(url);
		rb.method(HttpMethod.POST);
		return rb;
	}

	public static RequestBuilder2 post(String url, Map<String, String> headers, Map<String, String> data) {
		RequestBuilder2 rb = post(url);
		rb.headers(headers);
		rb.data(data);
		return rb;
	}

	public static RequestBuilder2 put(String url) {
		RequestBuilder2 rb = new Connection(url);
		rb.method(HttpMethod.PUT);
		return rb;
	}

	public static RequestBuilder2 put(String url, Map<String, String> headers, Map<String, String> data) {
		RequestBuilder2 rb = put(url);
		rb.headers(headers);
		rb.data(data);
		return rb;
	}

	public static RequestBuilder2 head(String url) {
		RequestBuilder2 rb = new Connection(url);
		rb.method(HttpMethod.HEAD);
		return rb;
	}

	public static RequestBuilder2 head(String url, Map<String, String> headers, Map<String, String> data) {
		RequestBuilder2 rb = head(url);
		rb.headers(headers);
		rb.data(data);
		return rb;
	}

	public static RequestBuilder2 delete(String url) {
		RequestBuilder2 rb = new Connection(url);
		rb.method(HttpMethod.DELETE);
		return rb;
	}

	public static RequestBuilder2 delete(String url, Map<String, String> headers, Map<String, String> data) {
		RequestBuilder2 rb = delete(url);
		rb.headers(headers);
		rb.data(data);
		return rb;
	}

	public static RequestBuilder2 options(String url) {
		RequestBuilder2 rb = new Connection(url);
		rb.method(HttpMethod.OPTIONS);
		return rb;
	}

	public static RequestBuilder2 options(String url, Map<String, String> headers, Map<String, String> data) {
		RequestBuilder2 rb = options(url);
		rb.headers(headers);
		rb.data(data);
		return rb;
	}

	public static RequestBuilder2 patch(String url) {
		RequestBuilder2 rb = new Connection(url);
		rb.method(HttpMethod.PATCH);
		return rb;
	}

	public static RequestBuilder2 patch(String url, Map<String, String> headers, Map<String, String> data) {
		RequestBuilder2 rb = patch(url);
		rb.headers(headers);
		rb.data(data);
		return rb;
	}

	public static void main(String[] args) throws Exception {
		String url = "http://blog.csdn.net/joe_007/article/details/7939471";
		RequestBuilder2 rb = Requests.get(url);
		rb.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.835.163 Safari/535.1");
		HttpResponse response = rb.execute();
		System.out.println(response);
		System.in.read();
		FileUtils.writeFile(response.bytes(), new File("d:/sql/blog.csdn.net.html"), false);
	}

}
