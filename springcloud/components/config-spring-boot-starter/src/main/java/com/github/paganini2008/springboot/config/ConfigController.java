package com.github.paganini2008.springboot.config;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.paganini2008.devtools.collection.MapUtils;

/**
 * 
 * ConfigController
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-03
 * @version 1.0
 */
@RestController
@RequestMapping("/config")
public class ConfigController {

	@Autowired
	private ApplicationProperties applicationProperties;

	@Keeping
	@Value("${forExample:Test}")
	private String forExample;

	@GetMapping("")
	public ResultEntity<Map<Object, Object>> config() {
		return ResultEntity.onSuccess(MapUtils.sort(applicationProperties, (left, right) -> {
			String leftKey = (String) left.getKey();
			String rightKey = (String) right.getKey();
			return leftKey.compareTo(rightKey);
		}));
	}

	@GetMapping("/example")
	public ResultEntity<String> forExample() {
		return ResultEntity.onSuccess(forExample);
	}

}
