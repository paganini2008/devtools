package com.github.paganini2008.springworld.socketbird.transport;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.paganini2008.devtools.collection.Tuple;

/**
 * 
 * JsonToTupleMapping
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
public class JsonToTupleMapping implements Mapping<Tuple> {

	private static final ObjectMapper mapper;

	static {
		mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	@SuppressWarnings("unchecked")
	public Tuple map(String data) {
		try {
			Map<String, Object> map = mapper.readValue(data, HashMap.class);
			return Tuple.wrap(map);
		} catch (Exception e) {
			throw new TransformerException(e.getMessage(), e);
		}
	}

}
