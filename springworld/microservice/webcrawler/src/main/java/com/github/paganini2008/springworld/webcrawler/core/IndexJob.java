package com.github.paganini2008.springworld.webcrawler.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import com.github.paganini2008.springworld.webcrawler.search.IndexedResourceService;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * IndexJob
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "webcrawler.indexer.job.enabled", havingValue = "true")
public class IndexJob {

	@Autowired
	private IndexedResourceService indexedResourceService;

	@Scheduled(cron = "0 0 1 * * ?")
	public void run() {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		log.info("Start to index ... ");
		indexedResourceService.indexAll();
		stopWatch.stop();
		log.info("Index end. Running for " + stopWatch.toString());
	}

}
