package com.github.paganini2008.springcloud.security.utils;

import com.google.gson.Gson;

/**
 * 
 * GsonUtils
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class GsonUtils {

	private static final Gson gson = new Gson();

	public static <T> T readObject(String jsonString, Class<T> requiredType) {
		return gson.fromJson(jsonString, requiredType);
	}

	public static String parseObject(Object object) {
		return gson.toJson(object);
	}

	private GsonUtils() {
	}
}
