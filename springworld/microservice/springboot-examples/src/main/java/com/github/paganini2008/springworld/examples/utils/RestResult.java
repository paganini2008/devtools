package com.github.paganini2008.springworld.examples.utils;

import java.util.HashMap;

/**
 * 
 * RestResult
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
public class RestResult extends HashMap<String, Object> {

	private static final long serialVersionUID = 1L;

	RestResult(boolean success, String msg, Object data) {
		put("success", success);
		put("msg", msg);
		put("data", data);
	}

	public static RestResult success() {
		return success(null);
	}

	public static RestResult success(Object data) {
		return success("ok", data);
	}

	public static RestResult success(String msg, Object data) {
		return new RestResult(true, msg, data);
	}

	public static RestResult failure() {
		return failure(null);
	}

	public static RestResult failure(String msg) {
		return new RestResult(false, msg, null);
	}

}
