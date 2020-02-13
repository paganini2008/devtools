package com.github.paganini2008.devtools.db4j;

/**
 * TokenParser
 * 
 * @author Fred Feng
 * @version 1.0
 */
public interface TokenParser {

	ParsedSql parse(String text);

}
