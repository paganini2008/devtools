package com.github.paganini2008.springworld.webcrawler.dao;

import com.github.paganini2008.devtools.jdbc.PageResponse;
import com.github.paganini2008.springworld.webcrawler.utils.PageBean;
import com.github.paganini2008.springworld.webcrawler.utils.Resource;
import com.github.paganini2008.springworld.webcrawler.utils.Source;
import com.github.paganini2008.springworld.webcrawler.utils.SourceIndex;

/**
 * 
 * ResourceService
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
public interface ResourceService {
	
	PageBean<Source> pageForSource(int page, int size);
	
	int updateSourceIndex(SourceIndex sourceIndex);
	
	int saveSourceIndex(SourceIndex sourceIndex);

	int saveSource(Source source);
	
	void deleteSource(String id);
	
	Source getSource(String id);
	
	int saveResource(Resource resource);
	
	PageResponse<Resource> queryForResource(int page, int size);

}
