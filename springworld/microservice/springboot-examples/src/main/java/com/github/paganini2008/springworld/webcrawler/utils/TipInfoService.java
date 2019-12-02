package com.github.paganini2008.springworld.webcrawler.utils;

import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

/**
 * 
 * TipInfoService
 * 
 * @author Fred Feng
 * @create 2019-03
 */
public class TipInfoService {

	private final TipProperties tipProperties = new TipProperties();

	public void start(int interval) throws Exception {
		tipProperties.setInterval(interval, TimeUnit.SECONDS);
	}

	public void setDataSource(DataSource dataSource) {
		tipProperties.setDataSource(dataSource);
	}

	public String getTipMsg(String code, String defaultMsg) {
		return tipProperties.getProperty(code, defaultMsg);
	}

	public void close() {
		tipProperties.clearInterval();
	}

}
