package com.github.paganini2008.devtools.logging;

/**
 * TokenParser
 * 
 * @author Fred Feng
 * @version 1.0
 */
public interface TokenParser {

	String parse(String text, Object... args);

}