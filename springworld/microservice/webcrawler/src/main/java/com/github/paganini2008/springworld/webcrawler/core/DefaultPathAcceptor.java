package com.github.paganini2008.springworld.webcrawler.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.collection.CollectionUtils;
import com.github.paganini2008.devtools.collection.MapUtils;
import com.github.paganini2008.springworld.webcrawler.jdbc.ResourceService;
import com.github.paganini2008.springworld.webcrawler.utils.Source;
import com.github.paganini2008.transport.Tuple;

/**
 * 
 * DefaultPathAcceptor
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
public class DefaultPathAcceptor implements PathAcceptor {

	@Qualifier("crawlerPathMatcher")
	@Autowired
	private PathMatcher pathMather;

	@Autowired
	private ResourceService resourceService;

	private final Map<Long, List<String>> includedPathPatternCache = new ConcurrentHashMap<Long, List<String>>();
	private final Map<Long, List<String>> excludedPathPatternCache = new ConcurrentHashMap<Long, List<String>>();

	@SuppressWarnings("unchecked")
	@Override
	public boolean accept(String refer, String path, Tuple tuple) {
		long sourceId = (Long) tuple.getField("sourceId");
		List<String> pathPatterns = MapUtils.get(excludedPathPatternCache, sourceId, () -> {
			Source source = resourceService.getSource(sourceId);
			if (StringUtils.isBlank(source.getExcludedPathPattern())) {
				return Collections.EMPTY_LIST;
			}
			return Arrays.asList(source.getExcludedPathPattern().split(","));
		});
		for (String pathPattern : pathPatterns) {
			if (pathMather.match(pathPattern, path)) {
				return false;
			}
		}

		pathPatterns = MapUtils.get(includedPathPatternCache, sourceId, () -> {
			Source source = resourceService.getSource(sourceId);
			if (StringUtils.isBlank(source.getPathPattern())) {
				return Collections.EMPTY_LIST;
			}
			return Arrays.asList(source.getPathPattern().split(","));
		});
		if (CollectionUtils.isEmpty(pathPatterns)) {
			return path.startsWith(refer);
		}
		for (String pathPattern : pathPatterns) {
			if (pathMather.match(pathPattern, path)) {
				return true;
			}
		}
		return false;
	}

	public static void main(String[] args) {
		PathMatcher pathMather = new AntPathMatcher();
		final String pattern = "http://www.baidu.com/a/";
		System.out.println(pathMather.match(pattern, "http://www.baidu.com/a/b/c/"));
	}

}
