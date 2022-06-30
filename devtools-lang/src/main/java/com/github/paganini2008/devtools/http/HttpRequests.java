/**
* Copyright 2017-2022 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.github.paganini2008.devtools.http;

/**
 * 
 * HttpRequests
 * 
 * @author Fred Feng
 * 
 * 
 * @since 2.0.1
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
