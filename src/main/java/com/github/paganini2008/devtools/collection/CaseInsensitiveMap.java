package com.github.paganini2008.devtools.collection;

import java.util.Map;

import com.github.paganini2008.devtools.Case;
import com.github.paganini2008.devtools.Cases;

/**
 * 
 * CaseInsensitiveMap
 * 
 * @author Fred Feng
 * @created 2018-03
 */
public class CaseInsensitiveMap<V> extends CaseFormatInsensitiveMap<V> {

	private static final long serialVersionUID = -7106695869199920528L;
	private static final Case DEFUALT_CASE = Cases.LOWER;

	public CaseInsensitiveMap() {
		super(DEFUALT_CASE);
	}

	public CaseInsensitiveMap(Map<String, V> m) {
		super(m, DEFUALT_CASE);
	}

}
