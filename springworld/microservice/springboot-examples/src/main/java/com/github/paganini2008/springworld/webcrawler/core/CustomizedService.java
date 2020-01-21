package com.github.paganini2008.springworld.webcrawler.core;

import com.github.paganini2008.springworld.webcrawler.utils.Resource;
import com.github.paganini2008.springworld.webcrawler.utils.Source;

/**
 * 
 * CustomizedService
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
@FunctionalInterface
public interface CustomizedService {

	void customize(Source source, Resource resource);

}
