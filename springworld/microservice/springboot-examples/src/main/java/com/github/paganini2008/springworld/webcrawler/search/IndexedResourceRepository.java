package com.github.paganini2008.springworld.webcrawler.search;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

/**
 * 
 * IndexedResourceRepository
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
@Component
public interface IndexedResourceRepository extends ElasticsearchRepository<IndexedResource, Long> {

}
