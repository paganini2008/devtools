package com.github.paganini2008.springworld.webcrawler.core;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * 
 * HtmlUnitPageSource
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
public class HtmlUnitPageSource implements PageSource {

	private WebClient webClient;

	@Override
	public void configure() {
		webClient = new WebClient(BrowserVersion.CHROME);
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
	}

	@Override
	public synchronized String getHtml(String url) throws Exception {
		HtmlPage page = webClient.getPage(url);
		return page.asXml();
	}

	@Override
	public void destroy() {
		if (webClient != null) {
			webClient.close();
		}
	}

	public static void main(String[] args) throws Exception {
		HtmlUnitPageSource pageSource = new HtmlUnitPageSource();
		pageSource.configure();
		//System.out.println(pageSource.getHtml("https://blog.csdn.net/u010814849/article/details/52526705"));
		System.out.println(pageSource.getHtml("https://www.tuniu.com/tours/"));
		System.in.read();
		pageSource.destroy();
	}

}
