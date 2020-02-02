package com.github.paganini2008.devtools.db4j;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.rowset.CachedRowSet;

import com.github.paganini2008.devtools.db4j.mapper.RowMapper;
import com.sun.rowset.CachedRowSetImpl;

/**
 * DetachedIteratorResultSetExtractor
 * 
 * @author Fred Feng
 * @version 1.0
 */
@SuppressWarnings("restriction")
public class DetachedIteratorResultSetExtractor<T> implements ResultSetExtractor<Iterator<T>> {

	private final RowMapper<T> rowMapper;

	DetachedIteratorResultSetExtractor(RowMapper<T> rowMapper) {
		if (rowMapper == null) {
			throw new IllegalArgumentException("RowMapper must not be null.");
		}
		this.rowMapper = rowMapper;
	}

	
	public Iterator<T> extractData(ResultSet rs) throws SQLException {
		final CachedRowSet delegate = new CachedRowSetImpl();
		delegate.populate(rs);
		configure(delegate);
		final AtomicInteger index = new AtomicInteger(0);
		return new Iterator<T>() {

			public boolean hasNext() {
				try {
					return delegate.next();
				} catch (SQLException e) {
					throw new IllegalStateException(e.getMessage(), e);
				}
			}

			public T next() {
				try {
					return rowMapper.mapRow(index.incrementAndGet(), delegate);
				} catch (SQLException e) {
					throw new IllegalStateException(e.getMessage(), e);
				}
			}
		};
	}

	protected void configure(ResultSet delegate) {
	}

}
