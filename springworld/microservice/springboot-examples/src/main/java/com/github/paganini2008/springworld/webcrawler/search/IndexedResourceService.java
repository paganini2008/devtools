package com.github.paganini2008.springworld.webcrawler.search;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Component;

import com.github.paganini2008.devtools.jdbc.PageResponse;
import com.github.paganini2008.springworld.webcrawler.jdbc.ResourceService;
import com.github.paganini2008.springworld.webcrawler.utils.Resource;
import com.github.paganini2008.springworld.webcrawler.utils.Source;
import com.github.paganini2008.springworld.webcrawler.utils.SourceIndex;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * IndexedResourceService
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
@Slf4j
@Component
public class IndexedResourceService {

	@Autowired
	private IndexedResourceRepository indexedResourceRepository;

	@Autowired
	private ResourceService resourceService;

	@Autowired
	private ElasticsearchTemplate elasticsearchTemplate;

	public void createIndex() {
		elasticsearchTemplate.createIndex("crawler_resources");
	}

	public void deleteIndex() {
		elasticsearchTemplate.deleteIndex("ind_webcrawler_resource");
	}

	public void saveResource(IndexedResource indexedResource) {
		indexedResourceRepository.save(indexedResource);
	}

	public void deleteResource(IndexedResource indexedResource) {
		indexedResourceRepository.delete(indexedResource);
	}

	public long indexCount() {
		return indexedResourceRepository.count();
	}

	public void indexAll() {
		int page = 1;
		PageResponse<Source> pageResponse;
		do {
			pageResponse = resourceService.queryForSource(page, 10);
			for (Source source : pageResponse.getContent()) {
				indexAll(source.getId());
			}
			page++;
		} while (pageResponse.hasNextPage());

	}

	public void indexAll(long id) {
		Source source = resourceService.getSource(id);
		int page = 1;
		PageResponse<Resource> pageResponse;
		do {
			pageResponse = resourceService.queryForResource(source.getId(), page, 100);
			for (Resource resource : pageResponse.getContent()) {
				try {
					index(source, resource);
					if (log.isInfoEnabled()) {
						log.info("Index resource: " + resource.toString());
					}
				} catch (RuntimeException e) {
					log.error(e.getMessage(), e);
				}
			}
			page++;
		} while (pageResponse.hasNextPage());

		SourceIndex sourceIndex = resourceService.getSourceIndex(id);
		resourceService.updateResourceVersion(source.getId(), sourceIndex.getVersion());
		log.info("Create indexes on source: " + source.getName() + " ok. Current index size: " + indexCount());
	}

	public void index(Source source, Resource resource) {
		IndexedResource indexedResource = new IndexedResource();
		Document document = Jsoup.parse(resource.getHtml());
		indexedResource.setId(resource.getId());
		indexedResource.setTitle(resource.getTitle());
		indexedResource.setContent(document.body().text());
		indexedResource.setOrder(document.body().select("a").size());
		indexedResource.setPath(resource.getUrl());
		indexedResource.setType(resource.getType());
		indexedResource.setUrl(source.getUrl());
		indexedResource.setSource(source.getName());
		indexedResource.setCreateDate(resource.getCreateDate().getTime());
		indexedResourceRepository.save(indexedResource);
	}

}
