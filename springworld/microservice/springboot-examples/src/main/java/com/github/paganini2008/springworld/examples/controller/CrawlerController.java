package com.github.paganini2008.springworld.examples.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.paganini2008.springworld.examples.utils.RestResult;
import com.github.paganini2008.springworld.socketbird.Tuple;
import com.github.paganini2008.springworld.socketbird.transport.NioClient;
import com.github.paganini2008.springworld.socketbird.utils.Partitioner;

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

	@GetMapping("/crawler/submit")
	public RestResult submit(@RequestParam("url") String url) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("refer", url);
		data.put("path", url);
		nioClient.send(Tuple.createBy(data), partitioner);
		return RestResult.success("Submit url ok", null);
	}

}
