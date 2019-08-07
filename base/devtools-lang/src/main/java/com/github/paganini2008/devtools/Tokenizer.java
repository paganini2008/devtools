package com.github.paganini2008.devtools;

import java.util.function.Function;

/**
 * 
 * Tokenizer
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 * @version 1.0
 */
public interface Tokenizer<T, R> {

	R parse(String text, Function<T, Object> function);

}