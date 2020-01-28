package com.github.paganini2008.springworld.webcrawler.utils;

import java.util.LinkedHashMap;

/**
 * 
 * Reply
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
public class Reply extends LinkedHashMap<String, Object> {

	private static final long serialVersionUID = 1L;

	Reply(boolean success, String msg, Object data) {
		put("success", success);
		put("msg", msg);
		put("data", data);
	}

	public static Reply success(String message) {
		return success(message, null);
	}

	public static Reply success(Object data) {
		return success("ok", data);
	}

	public static Reply success(String msg, Object data) {
		return new Reply(true, msg, data);
	}

	public static Reply failure() {
		return failure(null);
	}

	public static Reply failure(String msg) {
		return new Reply(false, msg, null);
	}

}
