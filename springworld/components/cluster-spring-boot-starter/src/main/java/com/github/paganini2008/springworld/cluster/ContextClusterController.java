package com.github.paganini2008.springworld.cluster;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.paganini2008.springworld.cluster.implementor.InstanceId;

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
	private InstanceId instanceId;

	@GetMapping("/info")
	public Map<String, Object> info() {
		Map<String, Object> info = new HashMap<String, Object>();
		info.put("instanceId", instanceId.get());
		info.put("master", instanceId.isMaster());
		return info;
	}

}
