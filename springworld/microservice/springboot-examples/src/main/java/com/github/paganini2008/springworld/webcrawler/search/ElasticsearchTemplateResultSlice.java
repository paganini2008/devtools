package com.github.paganini2008.springworld.webcrawler.search;

import static com.github.paganini2008.springworld.webcrawler.search.SearchResult.SEARCH_FIELD_CONTENT;
import static com.github.paganini2008.springworld.webcrawler.search.SearchResult.SEARCH_FIELD_TITLE;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import com.github.paganini2008.devtools.beans.BeanUtils;
import com.github.paganini2008.devtools.jdbc.ResultSetSlice;

/**
 * 
 * ElasticsearchTemplateResultSlice
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
public class ElasticsearchTemplateResultSlice implements ResultSetSlice<SearchResult> {

	private final String keyword;
	private final ElasticsearchTemplate elasticsearchTemplate;

	public ElasticsearchTemplateResultSlice(String keyword, ElasticsearchTemplate elasticsearchTemplate) {
		this.keyword = keyword;
		this.elasticsearchTemplate = elasticsearchTemplate;
	}

	@Override
	public int totalCount() {
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery().should(QueryBuilders.matchQuery(SEARCH_FIELD_TITLE, keyword))
				.should(QueryBuilders.matchQuery(SEARCH_FIELD_CONTENT, keyword));
		NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build();
		return (int) elasticsearchTemplate.count(searchQuery, IndexedResource.class);
	}

	@Override
	public List<SearchResult> list(int maxResults, int firstResult) {
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery().should(QueryBuilders.matchQuery(SEARCH_FIELD_TITLE, keyword))
				.should(QueryBuilders.matchQuery(SEARCH_FIELD_CONTENT, keyword));
		NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
				.withHighlightFields(new HighlightBuilder.Field(SEARCH_FIELD_TITLE), new HighlightBuilder.Field(SEARCH_FIELD_CONTENT))
				.withHighlightBuilder(new HighlightBuilder().preTags("<font class=\"search-keyword\" color=\"#FF0000\">")
						.postTags("</font>").fragmentSize(10).numOfFragments(3).noMatchSize(150));
		if (maxResults > 0) {
			searchQueryBuilder = searchQueryBuilder.withPageable(PageRequest.of(firstResult / maxResults, maxResults));
		}
		AggregatedPage<IndexedResource> page = elasticsearchTemplate.queryForPage(searchQueryBuilder.build(), IndexedResource.class,
				new HighlightResultMapper(elasticsearchTemplate.getElasticsearchConverter().getMappingContext()));
		List<IndexedResource> content = page.getContent();
		List<SearchResult> dataList = new ArrayList<SearchResult>();
		for (IndexedResource resource : content) {
			dataList.add(BeanUtils.copy(resource, SearchResult.class, null));
		}
		return dataList;
	}

}
