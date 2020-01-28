package com.github.paganini2008.springworld.webcrawler.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.paganini2008.devtools.jdbc.PageResponse;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;
import com.github.paganini2008.springworld.webcrawler.search.IndexedResourceService;
import com.github.paganini2008.springworld.webcrawler.search.SearchResult;
import com.github.paganini2008.springworld.webcrawler.utils.Reply;

/**
 * 
 * SearchController
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
@RequestMapping("/search")
@RestController
public class SearchController {

	@Autowired
	private IndexedResourceService indexedResourceService;

	@GetMapping("/index/create")
	public Reply createIndex(@RequestParam("indexName") String indexName) {
		indexedResourceService.createIndex(indexName);
		return Reply.success("Operation OK.");
	}

	@GetMapping("/index/delete")
	public Reply deleteIndex(@RequestParam("indexName") String indexName) {
		indexedResourceService.deleteIndex(indexName);
		return Reply.success("Operation OK.");
	}

	@GetMapping("/source/{id}/index")
	public Reply indexSource(@PathVariable("id") Long id) {
		ThreadUtils.runAsThread(() -> {
			indexedResourceService.indexAll(id);
		});
		return Reply.success("Operation OK.");
	}

	@GetMapping("/source/index/count")
	public Reply indexCount() {
		return Reply.success(indexedResourceService.indexCount());
	}

	@GetMapping("/search")
	public Reply search(@RequestParam("q") String keyword, @RequestParam(value = "page", defaultValue = "1", required = false) int page,
			@CookieValue(value = "DATA_LIST_SIZE", required = false, defaultValue = "10") int size) {
		PageResponse<SearchResult> pageResponse = indexedResourceService.search(keyword, page, size);
		return Reply.success(pageResponse.getContent());
	}

}
