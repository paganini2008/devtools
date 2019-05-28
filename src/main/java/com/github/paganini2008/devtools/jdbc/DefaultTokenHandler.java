package com.github.paganini2008.devtools.jdbc;

public class DefaultTokenHandler implements TokenHandler {

	public String handleToken(String token) {
		return "?";
	}

}
