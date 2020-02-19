package com.github.paganini2008.devtools;

import java.util.function.Function;

/**
 * 
 * TokenParser
 *
 * @author Fred Feng
 * @version 1.0
 */
public interface TokenParser<T, R> {

	R parse(String text, Function<T, Object> function);

}
