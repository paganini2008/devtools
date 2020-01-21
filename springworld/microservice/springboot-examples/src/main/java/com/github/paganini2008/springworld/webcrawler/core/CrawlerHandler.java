package com.github.paganini2008.springworld.webcrawler.core;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.collection.CollectionUtils;
import com.github.paganini2008.devtools.collection.MapUtils;
import com.github.paganini2008.springworld.socketbird.Tuple;
import com.github.paganini2008.springworld.socketbird.transport.Handler;
import com.github.paganini2008.springworld.socketbird.transport.NioClient;
import com.github.paganini2008.springworld.socketbird.utils.Partitioner;
import com.github.paganini2008.springworld.webcrawler.jdbc.ResourceService;
import com.github.paganini2008.springworld.webcrawler.search.IndexedResourceService;
import com.github.paganini2008.springworld.webcrawler.utils.RedisBloomFilter;
import com.github.paganini2008.springworld.webcrawler.utils.Resource;
import com.github.paganini2008.springworld.webcrawler.utils.Source;

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
	private PageSource pageSource;

	@Autowired
	private ResourceService resourceService;

	@Autowired
	private NioClient nioClient;

	@Autowired
	private Partitioner partitioner;

	@Autowired
	private PathAcceptor pathAcceptor;

	@Autowired
	private ResourceCounter resourceCounter;

	@Autowired
	private FinishCondition finishCondition;

	@Autowired
	private IndexedResourceService indexService;

	private final Map<Long, Source> sourceCache = new ConcurrentHashMap<Long, Source>();

	@Value("${webcrawler.crawler.depth:-1}")
	private int depth;

	public void onData(Tuple tuple) {
		final String action = (String) tuple.getField("action");
		if (StringUtils.isNotBlank(action)) {
			switch (action) {
			case "crawl":
				doCrawl(tuple);
				break;
			case "index":
				doIndex(tuple);
				break;
			}
		}
	}

	private void doCrawl(Tuple tuple) {
		if (finishCondition.shouldFinish(tuple)) {
			return;
		}
		final long sourceId = (Long) tuple.getField("sourceId");
		final String refer = (String) tuple.getField("refer");
		final String path = (String) tuple.getField("path");
		final String type = (String) tuple.getField("type");
		final int version = (Integer) tuple.getField("version");

		String fullContent = sourceId + "$" + refer + "$" + path + "$" + version;
		if (bloomFilter.mightContain(fullContent)) {
			return;
		} else {
			bloomFilter.put(fullContent);
			log.info("Crawl: " + tuple.toString());
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
		resource.setHtml(dom.html());
		resource.setUrl(path);
		resource.setType(type);
		resource.setCreateDate(new Date());
		resource.setVersion(version);
		resource.setSourceId(sourceId);
		resourceService.saveResource(resource);
		if (log.isTraceEnabled()) {
			log.trace("Save: " + resource);
		}
		Elements elements = dom.body().select("a");
		if (CollectionUtils.isNotEmpty(elements)) {
			String href;
			for (Element element : elements) {
				href = element.absUrl("href");
				if (StringUtils.isNotBlank(href) && acceptedPath(refer, href, tuple)) {
					sendRecursively(sourceId, refer, href, type, version);
				}
			}
		}

		resourceCounter.incrementCount(sourceId);

		if (version > 0) {
			sendIndex(sourceId, resource.getId());
		}
	}

	private void doIndex(Tuple tuple) {
		long sourceId = (Long) tuple.getField("sourceId");
		Source source = MapUtils.get(sourceCache, sourceId, () -> {
			return resourceService.getSource(sourceId);
		});
		long resourceId = (Long) tuple.getField("resourceId");
		Resource resource = resourceService.getResource(resourceId);
		indexService.index(source, resource);
		log.info("Index: " + resource.toString());
	}

	private boolean acceptedPath(String refer, String path, Tuple tuple) {
		if (!pathAcceptor.accept(refer, path, tuple)) {
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

	private void sendRecursively(long sourceId, String refer, String href, String type, int version) {
		String fullContent = sourceId + "$" + refer + "$" + href + "$" + version;
		if (!bloomFilter.mightContain(fullContent)) {
			Tuple tuple = Tuple.newTuple();
			tuple.setField("action", "crawl");
			tuple.setField("sourceId", sourceId);
			tuple.setField("refer", refer);
			tuple.setField("path", href);
			tuple.setField("type", type);
			tuple.setField("version", version);
			nioClient.send(tuple, partitioner);
		}
	}

	private void sendIndex(long sourceId, long resourceId) {
		Tuple tuple = Tuple.newTuple();
		tuple.setField("action", "index");
		tuple.setField("sourceId", sourceId);
		tuple.setField("resourceId", resourceId);
		nioClient.send(tuple, partitioner);
	}

}
