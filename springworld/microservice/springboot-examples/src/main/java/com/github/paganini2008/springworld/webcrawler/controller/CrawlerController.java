package com.github.paganini2008.springworld.webcrawler.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.paganini2008.springworld.socketbird.Tuple;
import com.github.paganini2008.springworld.socketbird.transport.NioClient;
import com.github.paganini2008.springworld.socketbird.utils.Partitioner;
import com.github.paganini2008.springworld.webcrawler.core.ResourceCounter;
import com.github.paganini2008.springworld.webcrawler.dao.ResourceService;
import com.github.paganini2008.springworld.webcrawler.utils.PageBean;
import com.github.paganini2008.springworld.webcrawler.utils.Reply;
import com.github.paganini2008.springworld.webcrawler.utils.Source;
import com.github.paganini2008.springworld.webcrawler.utils.SourceIndex;

/**
 * 
 * CrawlerController
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
@RequestMapping("/crawler")
@RestController
public class CrawlerController {

	@Autowired
	private NioClient nioClient;

	@Autowired
	private Partitioner partitioner;

	@Autowired
	private ResourceService resourceService;

	@Autowired
	private ResourceCounter resourceCounter;

	@DeleteMapping("/source/{id}/delete")
	public Reply deleteSource(@PathVariable("id") String id) {
		resourceService.deleteSource(id);
		return Reply.success("Delete OK.");
	}

	@PostMapping("/source/save")
	public Reply saveSource(@RequestBody Source source) {
		resourceService.saveSource(source);
		SourceIndex sourceIndex = new SourceIndex();
		sourceIndex.setSourceId(source.getId());
		sourceIndex.setVersion(1);
		resourceService.saveSourceIndex(sourceIndex);
		return Reply.success("Save OK.");
	}

	@GetMapping("/source")
	public Reply pageForSource(@RequestParam(value = "page", defaultValue = "1", required = false) int page,
			@CookieValue(value = "DATA_LIST_SIZE", required = false, defaultValue = "10") int size) {
		PageBean<Source> pageBean = resourceService.pageForSource(page, size);
		return Reply.success(pageBean);
	}

	@GetMapping("/task/{id}/submit")
	public Reply submit(@PathVariable("id") String id) {
		Source source = resourceService.getSource(id);
		resourceCounter.reset(source.getId());
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("sourceId", source.getId());
		data.put("refer", source.getUrl());
		data.put("path", source.getUrl());
		data.put("type", source.getType());
		data.put("version", 0);
		nioClient.send(Tuple.wrap(data), partitioner);
		return Reply.success("Submit OK.");
	}

}
