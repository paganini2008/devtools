package com.github.paganini2008.devtools.http;

/**
 * 
 * HttpRequests
 * 
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public abstract class HttpRequests {

	public static class GetBuilder extends HttpRequestBuilder {

		public GetBuilder(String url) {
			super("GET", url);
			request.doOutput(false);
			request.followRedirects(true);
		}

	}

	public static class HeadBuilder extends HttpRequestBuilder {

		public HeadBuilder(String url) {
			super("HEAD", url);
			request.doOutput(false);
			request.followRedirects(true);
		}

	}

	public static class DeleteBuilder extends HttpRequestBuilder {

		public DeleteBuilder(String url) {
			super("DELETE", url);
			request.doOutput(false);
			request.followRedirects(false);
		}

	}

	public static class PostBuilder extends HttpRequestBuilder {

		public PostBuilder(String url) {
			super("POST", url);
			request.doOutput(true);
			request.followRedirects(false);
		}

	}

	public static class PutBuilder extends HttpRequestBuilder {

		public PutBuilder(String url) {
			super("PUT", url);
			request.doOutput(true);
			request.followRedirects(false);
		}

	}

	public static class PatchBuilder extends HttpRequestBuilder {

		public PatchBuilder(String url) {
			super("PATCH", url);
			request.doOutput(true);
			request.followRedirects(false);
		}

	}

	public static class TraceBuilder extends HttpRequestBuilder {

		public TraceBuilder(String url) {
			super("TRACE", url);
			request.doOutput(true);
			request.followRedirects(false);
		}

	}

	public static class OptionsBuilder extends HttpRequestBuilder {

		public OptionsBuilder(String url) {
			super("OPTIONS", url);
			request.doOutput(true);
			request.followRedirects(false);
		}

	}

}
