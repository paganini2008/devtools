package com.github.paganini2008.springworld.webcrawler.jdbc;

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
	
	PageResponse<Source> queryForSource( int page, int size);

	PageBean<Source> pageForSource(int page, int size);

	int updateSourceIndex(SourceIndex sourceIndex);

	int saveSourceIndex(SourceIndex sourceIndex);

	int saveSource(Source source);

	void deleteSource(long id);

	Source getSource(long id);

	SourceIndex getSourceIndex(long id);

	int saveResource(Resource resource);

	Resource getResource(long id);

	PageResponse<Resource> queryForResource(long id, int page, int size);

	int updateResourceVersion(long sourceId, int version);

}
