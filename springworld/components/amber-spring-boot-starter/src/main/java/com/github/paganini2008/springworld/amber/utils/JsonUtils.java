package com.github.paganini2008.springworld.amber.utils;

import java.lang.reflect.Type;
import java.util.HashMap;

import com.github.paganini2008.springworld.amber.config.JobParameter;
import com.github.paganini2008.springworld.amber.config.JobParameterImpl;
import com.github.paganini2008.springworld.amber.test.HealthJobBean;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * 
 * JsonUtils
 * 
 * @author Fred Feng
 * @created 2019-03
 */
public class JsonUtils {

	private static final Gson gson;

	static {
		gson = new GsonBuilder().serializeNulls().registerTypeAdapter(Class.class, new ClassSerializer()).create();
	}

	static class ClassSerializer implements JsonSerializer<Class<?>>, JsonDeserializer<Class<?>> {

		ClassSerializer() {
		}

		public JsonElement serialize(Class<?> src, Type typeOfSrc, JsonSerializationContext context) {
			return src != null ? new JsonPrimitive(src.getName()) : null;
		}

		public Class<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			try {
				return json != null ? Class.forName(json.getAsString()) : null;
			} catch (ClassNotFoundException e) {
				throw new IllegalStateException(e);
			}
		}

	}

	public static <T> T readObject(String jsonString, Class<T> requiredType) {
		return gson.fromJson(jsonString, requiredType);
	}

	public static String parseObject(Object object) {
		return gson.toJson(object);
	}

	private JsonUtils() {
	}

	public static void main(String[] args) {
		JobParameter jobParameter = new JobParameterImpl("123", HealthJobBean.class, "tomcat", new HashMap<String, Object>());
		String str = parseObject(jobParameter);
		System.out.println(str);
		jobParameter = readObject(str, JobParameterImpl.class);
		System.out.println(jobParameter);
	}
}
