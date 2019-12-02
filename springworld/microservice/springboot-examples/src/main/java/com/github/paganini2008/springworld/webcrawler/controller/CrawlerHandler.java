package com.github.paganini2008.springworld.webcrawler.controller;

import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.PathMatcher;
import org.springframework.web.client.RestTemplate;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.collection.CollectionUtils;
import com.github.paganini2008.springworld.socketbird.Tuple;
import com.github.paganini2008.springworld.socketbird.TupleImpl;
import com.github.paganini2008.springworld.socketbird.transport.Handler;
import com.github.paganini2008.springworld.socketbird.transport.NioClient;
import com.github.paganini2008.springworld.socketbird.utils.Partitioner;
import com.github.paganini2008.springworld.webcrawler.config.RedisBloomFilter;
import com.github.paganini2008.springworld.webcrawler.dao.JdbcResourceService;
import com.github.paganini2008.springworld.webcrawler.utils.Resource;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * CrawlerHandler
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
@Slf4j
@Component
public class CrawlerHandler implements Handler {

	@Autowired
	private RedisBloomFilter bloomFilter;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private JdbcResourceService dataService;

	@Autowired
	private NioClient nioClient;

	@Autowired
	private Partitioner partitioner;

	@Qualifier("crawlerPathMatcher")
	@Autowired
	private PathMatcher pathMather;

	@Value("${webcrawler.crawl.depth:}")
	private int depth;

	@Override
	public void onData(Tuple tuple) {
		log.info("Handle: " + tuple.toString());
		final String refer = (String) tuple.getField("refer");
		final String path = (String) tuple.getField("path");
		if (bloomFilter.mightContain(path)) {
			return;
		}
		String html = null;
		try {
			html = restTemplate.getForObject(path, String.class);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		if (StringUtils.isBlank(html)) {
			return;
		}
		Document dom = Jsoup.parse(html);
		String title = dom.title();
		String body = dom.body().text();
		Resource resource = new Resource();
		resource.setTitle(title);
		resource.setContent(body);
		resource.setPath(path);
		resource.setCreateDate(new Date());
		dataService.saveResource(resource);

		Elements elements = dom.body().select("a");
		if (CollectionUtils.isNotEmpty(elements)) {
			String href;
			for (Element element : elements) {
				href = element.absUrl("href");
				if (StringUtils.isNotBlank(href) && acceptedPath(refer, href)) {
					sendRecursively(refer, href);
				}
			}
		}

	}

	private boolean acceptedPath(String refer, String path) {
		if (!path.startsWith(refer)) {
			return false;
		}
		if (!testDepth(refer, path)) {
			return false;
		}
		return true;
	}

	private boolean testDepth(String refer, String path) {
		if (depth <= 0) {
			return true;
		}
		String part = path.replace(refer, "");
		if (part.charAt(0) == '/') {
			part = part.substring(1);
		}
		int n = 0;
		for (char ch : part.toCharArray()) {
			if (ch == '/') {
				n++;
			}
		}
		return n <= depth;
	}

	private void sendRecursively(String refer, String href) {
		Tuple tuple = new TupleImpl();
		tuple.setField("refer", refer);
		tuple.setField("path", href);
		nioClient.send(tuple, partitioner);
	}

}
