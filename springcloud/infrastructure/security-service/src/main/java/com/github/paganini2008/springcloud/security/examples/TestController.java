package com.github.paganini2008.springcloud.security.examples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * TestController
 * 
 * @author Fred Feng
 * @revised 2019-06
 * @version 1.0
 */
@RestController
public class TestController {

	private static final Logger logger = LoggerFactory.getLogger(TestController.class);

	@GetMapping("/echo/{q}")
	public String echo(@PathVariable String q) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		logger.info(authentication.toString());
		return "Echo: " + q;
	}

	@GetMapping("/protect/{q}")
	public String protect(@PathVariable String q) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		logger.info(authentication.toString());
		return "Protected: " + q;
	}

}