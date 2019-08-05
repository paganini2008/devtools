package com.allyes.mec.cloud.security.utils;

import com.google.gson.Gson;

/**
 * 
 * JsonUtils
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class JsonUtils {

	private static final Gson gson = new Gson();

	public static <T> T readObject(String jsonString, Class<T> requiredType) {
		return gson.fromJson(jsonString, requiredType);
	}

	public static String parseObject(Object object) {
		return gson.toJson(object);
	}

	private JsonUtils() {
	}
}
