package com.github.paganini2008.springworld.cluster;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * ContextClusterController
 * 
 * @author Fred Feng
 * @created 2019-11
 * @revised 2019-11
 * @version 1.0
 */
@RequestMapping("/application/cluster")
@RestController
public class ContextClusterController {

	@Autowired
	private ClusterId clusterId;

	@Autowired
	private Environment env;

	@GetMapping("/info")
	public Map<String, Object> info() {
		Map<String, Object> info = new HashMap<String, Object>();
		info.put("clusterId", clusterId.get());
		info.put("master", clusterId.isMaster());
		info.put("port", env.getProperty("server.port"));
		return info;
	}

}
