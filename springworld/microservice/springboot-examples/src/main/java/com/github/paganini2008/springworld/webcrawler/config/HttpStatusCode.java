package com.github.paganini2008.springworld.webcrawler.config;

import org.springframework.http.HttpStatus;

/**
 * 
 * HttpStatusCode
 * 
 * @author Fred Feng
 * @revised 2019-06
 * @version 1.0
 */
public enum HttpStatusCode {

	BAD_REQUEST(HttpStatus.BAD_REQUEST, "错误的请求"),

	UNAUTHORIED(HttpStatus.UNAUTHORIZED, "未经授权的请求"),

	NOT_FOUND(HttpStatus.NOT_FOUND, "找不到资源"),

	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "服务器内部错误")

	, BAD_GATEWAY(HttpStatus.BAD_GATEWAY, "错误的网关"),

	GATEWAY_TIMEOUT(HttpStatus.GATEWAY_TIMEOUT, "网关超时");

	private final HttpStatus status;
	private final String represent;

	private HttpStatusCode(HttpStatus status, String represent) {
		this.status = status;
		this.represent = represent;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public String getRepresent() {
		return represent;
	}

	public static HttpStatusCode valueOf(int status) {
		return valueOf(HttpStatus.valueOf(status));
	}

	public static HttpStatusCode valueOf(HttpStatus httpStatus) {
		for (HttpStatusCode status : HttpStatusCode.values()) {
			if (status.getStatus().equals(httpStatus)) {
				return status;
			}
		}
		return HttpStatusCode.INTERNAL_SERVER_ERROR;
	}
}
