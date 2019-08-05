package com.github.paganini2008.springboot.authorization;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * 
 * AcceptedRequestFilter
 * 
 * @author Fred Feng
 * @revised 2019-06
 * @version 1.0
 */
public class AcceptedRequestFilter implements RequestMatcher {

	/**
	 * 
	 * RequestMatcher
	 * 
	 * @author Fred Feng
	 * @revised 2019-06
	 * @version 1.0
	 */
	public static interface HttpRequestMatcher {
		boolean matches(HttpServletRequest request);
	}

	private final List<HttpRequestMatcher> requestMatchers = new CopyOnWriteArrayList<HttpRequestMatcher>();

	public void addRequestMatcher(HttpRequestMatcher requestMatcher) {
		if (requestMatcher != null) {
			requestMatchers.add(requestMatcher);
		}
	}

	public void removeRequestMatcher(HttpRequestMatcher requestMatcher) {
		requestMatchers.remove(requestMatcher);
	}

	public boolean matches(HttpServletRequest request) {
		boolean result = false;
		for (HttpRequestMatcher requestMatcher : requestMatchers) {
			result |= requestMatcher.matches(request);
		}
		return result;
	}

}
