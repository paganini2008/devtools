package com.github.paganini2008.springworld.webcrawler.core;

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

import lombok.Setter;
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

	@Setter
	@Value("${webcrawler.crawler.selenium.webdriverExecutionPath}")
	private String webdriverExecutionPath;

	@Setter
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
		WebDriver webDriver = objectPool.borrowObject();
		int retries = 1;
		boolean failed = false;
		String html = null;
		do {
			try {
				webDriver.get(url);
				html = function.apply(webDriver);
			} catch (Exception e) {
				failed = true;
				log.error(e.getMessage(), e);
			}
		} while (failed && retries++ < requestRetries);
		if (failed) {
			objectPool.invalidateObject(webDriver);
		} else {
			objectPool.returnObject(webDriver);
		}
		return html;
	}

	class WebDriverObjectFactory extends BasePooledObjectFactory<WebDriver> {

		WebDriverObjectFactory() {
		}

		public WebDriver create() throws Exception {
			ChromeOptions options = new ChromeOptions();
			DesiredCapabilities cap = DesiredCapabilities.chrome();
			cap.setCapability(ChromeOptions.CAPABILITY, options);

			options.addArguments("lang=zh_CN.UTF-8");
			options.addArguments("user-agent=" + RandomUtils.randomChoice(userAgents));
			options.addArguments("--test-type", "--ignore-certificate-errors", "--start-maximized", "no-default-browser-check");
			options.addArguments("--headless", "--disable-gpu");
			ChromeDriver driver = new ChromeDriver(options);
			driver.setLogLevel(Level.OFF);
			driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS).implicitlyWait(60, TimeUnit.SECONDS).setScriptTimeout(60,
					TimeUnit.SECONDS);
			return driver;
		}

		public void destroyObject(PooledObject<WebDriver> object) throws Exception {
			object.getObject().quit();
		}

		public PooledObject<WebDriver> wrap(WebDriver object) {
			return new DefaultPooledObject<WebDriver>(object);
		}

	}

	public static void main(String[] args) throws Exception {
		String webdriverExecutionPath = "D:\\software\\chromedriver_win32\\chromedriver.exe";
		SeleniumPageSource pageSource = new SeleniumPageSource();
		pageSource.setRequestRetries(3);
		pageSource.setWebdriverExecutionPath(webdriverExecutionPath);
		pageSource.configure();
		System.out.println(pageSource.getHtml("https://www.howbuy.com"));
		System.in.read();
		pageSource.destroy();
	}

}
