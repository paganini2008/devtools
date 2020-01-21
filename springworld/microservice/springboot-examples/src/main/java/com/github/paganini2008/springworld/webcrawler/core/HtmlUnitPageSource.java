package com.github.paganini2008.springworld.webcrawler.core;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * HtmlUnitPageSource
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
@Slf4j
public class HtmlUnitPageSource implements PageSource {

	@Setter
	@Value("${webcrawler.pagesource.requestRetries:3}")
	private int requestRetries;

	private ObjectPool<WebClient> objectPool;

	public void configure() {
		GenericObjectPoolConfig<WebClient> poolConfig = new GenericObjectPoolConfig<WebClient>();
		poolConfig.setMinIdle(1);
		poolConfig.setMaxIdle(5);
		poolConfig.setMaxTotal(20);
		objectPool = new GenericObjectPool<WebClient>(new WebClientObjectFactory(), poolConfig);
	}

	public void destroy() {
		if (objectPool != null) {
			objectPool.close();
		}
	}

	class WebClientObjectFactory extends BasePooledObjectFactory<WebClient> {

		WebClientObjectFactory() {
		}

		public WebClient create() throws Exception {
			WebClient webClient = new WebClient(BrowserVersion.CHROME);
			webClient.getOptions().setThrowExceptionOnScriptError(false);
			webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
			webClient.getOptions().setActiveXNative(false);
			webClient.getOptions().setCssEnabled(false);
			webClient.getOptions().setJavaScriptEnabled(false);
			webClient.getOptions().setRedirectEnabled(false);
			webClient.getOptions().setUseInsecureSSL(true);
			webClient.setCookieManager(new CookieManager());
			webClient.setAjaxController(new NicelyResynchronizingAjaxController());
			webClient.getOptions().setTimeout(60 * 1000);
			webClient.setJavaScriptTimeout(60 * 1000);
			return webClient;
		}

		public void destroyObject(PooledObject<WebClient> object) throws Exception {
			object.getObject().close();
		}

		public PooledObject<WebClient> wrap(WebClient object) {
			return new DefaultPooledObject<WebClient>(object);
		}

	}

	@Override
	public String getHtml(String url) throws Exception {
		int retries = 0;
		boolean failed = false;
		WebClient webClient = objectPool.borrowObject();
		do {
			try {
				HtmlPage page = webClient.getPage(url);
				return page.asXml();
			} catch (Exception e) {
				failed = true;
				log.error(e.getMessage(), e);
			}
		} while (failed && retries++ < requestRetries);
		if (failed) {
			objectPool.invalidateObject(webClient);
		} else {
			objectPool.returnObject(webClient);
		}
		return "";
	}

	public static void main(String[] args) throws Exception {
		HtmlUnitPageSource pageSource = new HtmlUnitPageSource();
		pageSource.configure();
		// System.out.println(pageSource.getHtml("https://blog.csdn.net/u010814849/article/details/52526705"));
		System.out.println(pageSource.getHtml("http://www.ttmeishi.com/CaiXi/tese/"));
		System.in.read();
		pageSource.destroy();
	}

}
