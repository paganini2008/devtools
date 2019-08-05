package com.allyes.mec.cloud.security.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestMain {

	public static void main(String[] args) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String password = encoder.encode("123456");
		System.out.println(password);
	}

}
