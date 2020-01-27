package com.github.paganini2008.springworld.webcrawler.jdbc;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import com.github.paganini2008.devtools.jdbc.PageRequest;
import com.github.paganini2008.devtools.jdbc.PageResponse;
import com.github.paganini2008.devtools.jdbc.ResultSetSlice;
import com.github.paganini2008.springworld.webcrawler.utils.PageBean;
import com.github.paganini2008.springworld.webcrawler.utils.RedisIdentifier;
import com.github.paganini2008.springworld.webcrawler.utils.Resource;
import com.github.paganini2008.springworld.webcrawler.utils.Source;
import com.github.paganini2008.springworld.webcrawler.utils.SourceIndex;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * JdbcResourceService
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 */
@Slf4j
@Component
public class JdbcResourceService extends EnhancedNamedParameterJdbcDaoSupport implements ResourceService {

	private static final String SQL_SOURCE_INSERTION = "insert into crawler_source (id,name,url,path_pattern,type,create_date) values (:id,:name,:url,:pathPattern,:type,:createDate)";
	private static final String SQL_SOURCE_INDEX_INSERTION = "insert into crawler_source_index (id,source_id,version) values (:id,:sourceId,:version)";
	private static final String SQL_SOURCE_INDEX_UPDATE = "update crawler_source_index set version=:version where id=:id";
	private static final String SQL_SOURCE_ONE_SELECTION = "select * from crawler_source where id=:id limit 1";
	private static final String SQL_SOURCE_INDEX_ONE_SELECTION = "select * from crawler_source_index where source_id=:sourceId";
	private static final String SQL_SOURCE_DELETION = "delete from crawler_source where id=:id";
	private static final String SQL_SOURCE_SELECTION = "select * from crawler_source";
	private static final String SQL_RESOURCE_INSERTION = "insert into crawler_resources (id,title,html,url,type,create_date,version,source_id) values (:id,:title,:html,:url,:type,:createDate,:version,:sourceId)";
	private static final String SQL_RESOURCE_SELECTION = "select * from crawler_resources where source_id=:sourceId and version<(select version from crawler_source_index where source_id=:sourceId)";
	private static final String SQL_RESOURCE_ONE_SELECTION = "select * from crawler_resources where id=:id limit 1";
	private static final String SQL_RESOURCE_VERSION_UPDATE = "update crawler_resources set version=:version where source_id=:sourceId and version!=:version";

	@Autowired
	public void setSuperDataSource(final DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Autowired
	private RedisIdentifier redisId;

	public PageResponse<Source> queryForSource(int page, int size) {
		ResultSetSlice<Source> resultSetSlice = getNamedParameterJdbcTemplate().slice(SQL_SOURCE_SELECTION, null, Source.class);
		return resultSetSlice.list(PageRequest.of(page, size));
	}

	public PageBean<Source> pageForSource(int page, int size) {
		ResultSetSlice<Source> resultSetSlice = getNamedParameterJdbcTemplate().slice(SQL_SOURCE_SELECTION, null, Source.class);
		PageResponse<Source> response = resultSetSlice.list(PageRequest.of(page, size));
		PageBean<Source> pageBean = new PageBean<Source>();
		int rows = response.getTotalRecords();
		List<Source> content = response.getContent();
		pageBean.setPage(page);
		pageBean.setSize(size);
		pageBean.setRows(rows);
		pageBean.refresh();
		pageBean.setResults(content);
		return pageBean;
	}

	public int updateSourceIndex(SourceIndex sourceIndex) {
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(sourceIndex);
		try {
			return getNamedParameterJdbcTemplate().update(SQL_SOURCE_INDEX_UPDATE, paramSource);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return 0;
		}
	}

	public int saveSourceIndex(SourceIndex sourceIndex) {
		sourceIndex.setId(redisId.nextValue());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(sourceIndex);
		try {
			return getNamedParameterJdbcTemplate().update(SQL_SOURCE_INDEX_INSERTION, paramSource);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return 0;
		}
	}

	public int saveSource(Source source) {
		source.setId(redisId.nextValue());
		source.setCreateDate(new Date());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(source);
		try {
			return getNamedParameterJdbcTemplate().update(SQL_SOURCE_INSERTION, paramSource);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return 0;
		}
	}

	public void deleteSource(long id) {
		try {
			getNamedParameterJdbcTemplate().update(SQL_SOURCE_DELETION, new MapSqlParameterSource("id", id));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public Source getSource(long id) {
		return getNamedParameterJdbcTemplate().queryForObject(SQL_SOURCE_ONE_SELECTION, new MapSqlParameterSource("id", id),
				new BeanPropertyRowMapper<>(Source.class));
	}

	public SourceIndex getSourceIndex(long id) {
		return getNamedParameterJdbcTemplate().queryForObject(SQL_SOURCE_INDEX_ONE_SELECTION, new MapSqlParameterSource("sourceId", id),
				new BeanPropertyRowMapper<>(SourceIndex.class));
	}

	public int saveResource(Resource resource) {
		resource.setId(redisId.nextValue());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(resource);
		try {
			return getNamedParameterJdbcTemplate().update(SQL_RESOURCE_INSERTION, paramSource);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return 0;
		}
	}

	public Resource getResource(long id) {
		return getNamedParameterJdbcTemplate().queryForObject(SQL_RESOURCE_ONE_SELECTION, new MapSqlParameterSource("id", id),
				new BeanPropertyRowMapper<>(Resource.class));
	}

	public int updateResourceVersion(long sourceId, int version) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("sourceId", sourceId);
		paramMap.put("version", version);
		try {
			return getNamedParameterJdbcTemplate().update(SQL_RESOURCE_VERSION_UPDATE, paramMap);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return 0;
		}
	}

	public PageResponse<Resource> queryForResource(long id, int page, int size) {
		ResultSetSlice<Resource> resultSetSlice = getNamedParameterJdbcTemplate().slice(SQL_RESOURCE_SELECTION,
				new MapSqlParameterSource("sourceId", id), Resource.class);
		return resultSetSlice.list(PageRequest.of(page, size));
	}
}
