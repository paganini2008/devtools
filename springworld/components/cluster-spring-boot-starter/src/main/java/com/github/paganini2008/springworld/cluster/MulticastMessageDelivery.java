package com.github.paganini2008.springworld.cluster;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.paganini2008.springworld.cluster.implementor.ContextMulticastGroup;

/**
 * 
 * MulticastMessageDelivery
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
@RequestMapping("/app/cluster")
@RestController
public class MulticastMessageDelivery {

	@Autowired
	private ContextMulticastGroup multicastGroup;

	@GetMapping("/info")
	public Map<String, Object> info() {
		String content = multicastGroup.getClusterInfo();
		return resultMap(content);
	}

	@GetMapping("/multicast")
	public Map<String, Object> multicast(@RequestParam("c") String content) {
		multicastGroup.multicast(content);
		return resultMap(content);
	}

	@GetMapping("/unicast")
	public Map<String, Object> unicast(@RequestParam("c") String content) {
		multicastGroup.unicast(content);
		return resultMap(content);
	}

	private Map<String, Object> resultMap(String content) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("data", content);
		data.put("success", true);
		return data;
	}

}
