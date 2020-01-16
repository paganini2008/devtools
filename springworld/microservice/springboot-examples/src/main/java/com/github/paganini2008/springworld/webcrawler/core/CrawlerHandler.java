package com.github.paganini2008.springworld.webcrawler.core;

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

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.collection.CollectionUtils;
import com.github.paganini2008.springworld.socketbird.Tuple;
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
public class CrawlerHandler {

	@Autowired
	private RedisBloomFilter bloomFilter;

	@Autowired
	private PageSource pageSource;

	@Autowired
	private JdbcResourceService resourceService;

	@Autowired
	private NioClient nioClient;

	@Autowired
	private Partitioner partitioner;

	@Qualifier("crawlerPathMatcher")
	@Autowired
	private PathMatcher pathMather;

	@Value("${webcrawler.crawler.depth:-1}")
	private int depth;

	public void onData(Tuple tuple) {
		final String refer = (String) tuple.getField("refer");
		final String path = (String) tuple.getField("path");
		final String type = (String) tuple.getField("type");
		final int version = (Integer) tuple.getField("version");

		String fullContent = refer + "$" + path + "$" + version;
		if (bloomFilter.mightContain(fullContent)) {
			log.trace("Path '{}/{}' has saved.", refer, path);
			return;
		} else {
			bloomFilter.put(fullContent);
			log.info("Handle: " + tuple.toString());
		}
		String html = null;
		try {
			html = pageSource.getHtml(path);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		if (StringUtils.isBlank(html)) {
			return;
		}

		Document dom = Jsoup.parse(html);
		Resource resource = new Resource();
		resource.setTitle(dom.title());
		resource.setHtml(dom.body().html());
		resource.setUrl(path);
		resource.setType(type);
		resource.setCreateDate(new Date());
		resource.setVersion(version);
		resourceService.saveResource(resource);
		log.trace("Save: " + resource);

		Elements elements = dom.body().select("a");
		if (CollectionUtils.isNotEmpty(elements)) {
			String href;
			for (Element element : elements) {
				href = element.absUrl("href");
				if (StringUtils.isNotBlank(href) && acceptedPath(refer, href)) {
					sendRecursively(refer, href, type, version);
				}
			}
		}

	}

	private boolean acceptedPath(String refer, String path) {
		if (refer.contains("*")) {
			if (!pathMather.match(refer, path)) {
				return false;
			}
		} else {
			if (!path.startsWith(refer)) {
				return false;
			}
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

	private void sendRecursively(String refer, String href, String type, int version) {
		Tuple tuple = Tuple.newTuple();
		tuple.setField("refer", refer);
		tuple.setField("path", href);
		tuple.setField("type", type);
		tuple.setField("version", version);
		nioClient.send(tuple, partitioner);
	}

}
