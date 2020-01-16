package com.github.paganini2008.springworld.webcrawler.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.paganini2008.springworld.socketbird.Tuple;
import com.github.paganini2008.springworld.socketbird.transport.NioClient;
import com.github.paganini2008.springworld.socketbird.utils.Partitioner;
import com.github.paganini2008.springworld.webcrawler.utils.FreeProxyPool;
import com.github.paganini2008.springworld.webcrawler.utils.RestResult;

/**
 * 
 * CrawlerController
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
@RestController
public class CrawlerController {

	@Autowired
	private NioClient nioClient;

	@Autowired
	private Partitioner partitioner;

	@Autowired
	private FreeProxyPool freeProxyPool;

	@GetMapping("/crawler/freeproxy/update")
	public RestResult updateFreeProxyIp() {
		freeProxyPool.update();
		return RestResult.success("Operation OK.", null);
	}

	@GetMapping("/crawler/submit")
	public RestResult submit(@RequestParam("url") String url,
			@RequestParam(name = "version", required = false, defaultValue = "1") int version) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("refer", url);
		data.put("path", url);
		data.put("type", "美食");
		data.put("version", version);
		nioClient.send(Tuple.wrap(data), partitioner);
		return RestResult.success("Operation OK.", null);
	}

}
