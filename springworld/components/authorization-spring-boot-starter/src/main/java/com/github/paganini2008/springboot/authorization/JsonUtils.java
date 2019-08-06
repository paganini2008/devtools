package com.github.paganini2008.springboot.authorization;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import com.github.paganini2008.devtools.StringUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 
 * JsonUtils
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class JsonUtils {

	private static final Gson gson;

	static {
		gson = new GsonBuilder().serializeNulls().create();
	}

	public static <T> List<T> readAsList(String jsonString, Class<T> requiredType) {
		return StringUtils.isNotBlank(jsonString) ? gson.fromJson(jsonString, new ParameterizedTypeImpl<T>(requiredType)) : null;
	}

	public static <T> T readObject(String jsonString, Class<T> requiredType) {
		return StringUtils.isNotBlank(jsonString) ? gson.fromJson(jsonString, requiredType) : null;
	}

	public static String parseObject(Object object) {
		return object != null ? gson.toJson(object) : "";
	}

	private JsonUtils() {
	}

	/**
	 * 
	 * ParameterizedTypeImpl
	 * 
	 * @author Fred Feng
	 * @revised 2019-05
	 * @version 1.0
	 */
	private static class ParameterizedTypeImpl<T> implements ParameterizedType {

		Class<T> clazz;

		ParameterizedTypeImpl(Class<T> clz) {
			clazz = clz;
		}

		public Type[] getActualTypeArguments() {
			return new Type[] { clazz };
		}

		public Type getRawType() {
			return List.class;
		}

		public Type getOwnerType() {
			return null;
		}
	}

	public static void main(String[] args) {
	}
}
