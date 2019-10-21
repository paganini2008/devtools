package com.github.paganini2008.springworld.examples.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * TestController
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
@RestController
public class TestController {

	@GetMapping("/index")
	public Map<String, Object> echo() {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("data", "hello");
		return data;
	}

}
