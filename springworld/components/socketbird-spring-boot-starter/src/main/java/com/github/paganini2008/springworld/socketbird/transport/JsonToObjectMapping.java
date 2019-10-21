package com.github.paganini2008.springworld.socketbird.transport;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * 
 * JsonToObjectMapping
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
public class JsonToObjectMapping<T> implements Mapping<T> {

	private static final ObjectMapper mapper;

	static {
		mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	private final Class<T> objectClass;

	public JsonToObjectMapping(Class<T> objectClass) {
		this.objectClass = objectClass;
	}

	@Override
	public T map(String data) {
		try {
			return mapper.readValue(data, objectClass);
		} catch (Exception e) {
			throw new TransformerException(e.getMessage(), e);
		}
	}

}
