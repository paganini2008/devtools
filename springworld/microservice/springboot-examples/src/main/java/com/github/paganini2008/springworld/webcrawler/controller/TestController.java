package com.github.paganini2008.springworld.webcrawler.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.paganini2008.springworld.webcrawler.config.RedisBloomFilter;

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

	@Autowired
	private RedisBloomFilter bloomFilter;

	@GetMapping("/index")
	public Map<String, Object> echo() {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("data", "hello");
		return data;
	}

	@GetMapping("/test/bloom")
	public Map<String, Object> testBloomFilter(@RequestParam("content") String content) {
		Map<String, Object> result = new HashMap<String, Object>();
		boolean contain = bloomFilter.mightContain(content);
		result.put("contain", contain);
		result.put("success", true);
		if (!contain) {
			bloomFilter.put(content);
		}
		return result;
	}

}
