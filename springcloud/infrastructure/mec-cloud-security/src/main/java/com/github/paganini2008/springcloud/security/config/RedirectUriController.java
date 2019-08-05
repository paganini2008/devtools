package com.github.paganini2008.springcloud.security.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * RedirectUriController
 * 
 * @author Fred Feng
 * @revised 2019-06
 * @version 1.0
 */
@RestController
public class RedirectUriController {

	@GetMapping("/done")
	public Map<String, Object> done(@RequestParam("code") String code, @RequestParam(value = "state", required = false) String state) {
		Map<String, Object> data = new HashMap<String,Object>();
		data.put("code", code);
		data.put("state", state);
		return data;
	}

}
