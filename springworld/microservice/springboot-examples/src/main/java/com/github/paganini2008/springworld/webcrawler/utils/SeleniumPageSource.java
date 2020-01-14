package com.github.paganini2008.springworld.webcrawler.utils;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.logging.Level;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.beans.factory.annotation.Value;

import com.github.paganini2008.devtools.RandomUtils;
import com.github.paganini2008.devtools.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * SeleniumPageSource
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
@Slf4j
public class SeleniumPageSource implements PageSource {

	@Value("${webcrawler.crawler.selenium.webdriverExecutionPath}")
	private String webdriverExecutionPath;

	@Value("${webcrawler.crawler.requestRetries:3}")
	private int requestRetries;

	private ObjectPool<WebDriver> objectPool;

	public void configure() {
		System.setProperty("webdriver.chrome.driver", webdriverExecutionPath);

		GenericObjectPoolConfig<WebDriver> poolConfig = new GenericObjectPoolConfig<WebDriver>();
		poolConfig.setMinIdle(1);
		poolConfig.setMaxIdle(5);
		poolConfig.setMaxTotal(10);
		objectPool = new GenericObjectPool<WebDriver>(new WebDriverObjectFactory(), poolConfig);
	}

	public void destroy() {
		if (objectPool != null) {
			objectPool.close();
		}
	}

	public String getHtml(String url) throws Exception {
		return getHtml(url, webdriver -> {
			return webdriver.getPageSource();
		});
	}

	public String getHtml(String url, Function<WebDriver, String> function) throws Exception {
		if (StringUtils.isBlank(url)) {
			throw new IllegalArgumentException("Url must not be blank.");
		}
		WebDriver webDriver = null;
		int retries = 1;
		boolean failed = false;
		do {
			try {
				webDriver = objectPool.borrowObject();
				log.info("Request to: " + url);
				webDriver.get(url);
				return function.apply(webDriver);
			} catch (Exception e) {
				failed = true;
				if (retries++ > requestRetries) {
					throw e;
				}
				log.error("Retries: " + retries + ", Cause: " + e.getMessage(), e);
			} finally {
				if (webDriver != null) {
					if (retries > requestRetries) {
						objectPool.invalidateObject(webDriver);
					} else {
						objectPool.returnObject(webDriver);
					}
				}
			}
		} while (failed);
		throw new IllegalStateException("Exceed max request times.");
	}

	private static class WebDriverObjectFactory extends BasePooledObjectFactory<WebDriver> {

		WebDriverObjectFactory() {
		}

		public WebDriver create() throws Exception {
			DesiredCapabilities cap = DesiredCapabilities.chrome();
			String userAgent = RandomUtils.randomChoice(userAgents);
			cap.setCapability("User-Agent", userAgent);
			cap.setCapability("X-Forwarded-For", IpUtils.getRandomIp());
			ChromeOptions options = new ChromeOptions();
			options.merge(cap);
			options.addArguments("--test-type", "--ignore-certificate-errors", "--start-maximized", "no-default-browser-check");
			options.addArguments("--headless", "--disable-gpu");
			ChromeDriver driver = new ChromeDriver(options);
			driver.setLogLevel(Level.OFF);
			driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS).implicitlyWait(60, TimeUnit.SECONDS);
			return driver;
		}

		public void destroyObject(PooledObject<WebDriver> object) throws Exception {
			object.getObject().quit();
		}

		public PooledObject<WebDriver> wrap(WebDriver object) {
			return new DefaultPooledObject<WebDriver>(object);
		}

	}

}
