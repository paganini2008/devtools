package com.github.paganini2008.springworld.webcrawler.utils;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;
import com.github.paganini2008.springworld.webcrawler.core.PageSource;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * KuaiDaiLiFreeProxyPool
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
@Slf4j
@Component
public class FreeProxyPool {

	private static final String kuaiDaiLiFreeProxyHome = "https://www.kuaidaili.com/free/inha/%s/";
	private static final int maxFreeProxyIpSize = 100;

	@Autowired
	private PageSource pageSource;

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Value("${webcrawler.redis-key.freeProxyAddress:free_proxy_address}")
	private String freeProxyAddressRedisKey;

	public SocketAddress getRandomProxy() {
		long size = redisTemplate.opsForList().size(freeProxyAddressRedisKey);
		if (size > 0) {
			String location = redisTemplate.opsForList().index(freeProxyAddressRedisKey, ThreadLocalRandom.current().nextLong(size));
			if (StringUtils.isNotBlank(location)) {
				String[] args = location.split(":");
				return new InetSocketAddress(args[0], Integer.parseInt(args[1]));
			}
		}
		return null;
	}

	public void update() {
		redisTemplate.delete(freeProxyAddressRedisKey);
		update(1, new AtomicInteger(0));
	}

	public void update(int page, AtomicInteger total) {
		String url = String.format(kuaiDaiLiFreeProxyHome, page);
		String html = null;
		try {
			html = pageSource.getHtml(url);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		if (StringUtils.isBlank(html)) {
			return;
		}
		Document document = Jsoup.parse(html);
		Element table = document.body().selectFirst("table");
		if (table == null) {
			return;
		}
		Elements trs = table.select("tbody tr");
		if (trs == null) {
			return;
		}
		Elements tds;
		Element td;
		String value;
		boolean work = true;
		for (Element tr : trs) {
			tds = tr.select("td");
			td = tds.get(0);
			value = td.text();
			td = tds.get(1);
			value += ":" + td.text();
			redisTemplate.opsForList().leftPush(freeProxyAddressRedisKey, value);
			if (total.incrementAndGet() >= maxFreeProxyIpSize) {
				work = false;
				break;
			}
		}
		if (work) {
			ThreadUtils.randomSleep(1000, 5000);
			update(page + 1, total);
		}
	}

}
