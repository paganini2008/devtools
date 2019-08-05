package com.allyes.components.jpahelper.deprecated;

import java.util.List;
import java.util.Map;

import org.springframework.data.repository.NoRepositoryBean;

import com.allyes.components.jpahelper.support.EntityDao;

/**
 * 
 * BaseDao <br/>
 * Please use EntityDao instead of BaseDao
 * 
 * @author Fred Feng
 * @version 1.0
 */
@Deprecated
@NoRepositoryBean
public interface BaseDao<E, ID> extends EntityDao<E, ID> {

	@Deprecated
	Object getSingleResult(String sql, Object[] arguments);

	@Deprecated
	List<Map<String, ?>> queryForMap(String sql, Object[] arguments);

	@Deprecated
	List<Map<String, ?>> queryForMap(String sql, Object[] arguments, int offset, int limit);

	@Deprecated
	E queryOne(String sql, Object[] arguments);

	@Deprecated
	List<E> query(String sql, Object[] arguments);

	@Deprecated
	List<E> query(String sql, Object[] arguments, int offset, int limit);

	@Deprecated
	<T> T queryOne(String sql, Object[] arguments, Class<T> resultClass);

	@Deprecated
	<T> List<T> query(String sql, Object[] arguments, Class<T> resultClass);

	@Deprecated
	<T> List<T> query(String sql, Object[] arguments, int offset, int limit, Class<T> resultClass);

}
