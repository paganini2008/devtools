package com.github.paganini2008.devtools.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLong;

import javax.sql.rowset.CachedRowSet;

import com.github.paganini2008.devtools.jdbc.mapper.RowMapper;
import com.sun.rowset.CachedRowSetImpl;

/**
 * DetachedIteratorResultSetExtractor
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class DetachedIteratorResultSetExtractor<T> implements ResultSetExtractor<Iterator<T>> {

	private final RowMapper<T> rowMapper;

	DetachedIteratorResultSetExtractor(RowMapper<T> rowMapper) {
		if (rowMapper == null) {
			throw new IllegalArgumentException("RowMapper must not be null.");
		}
		this.rowMapper = rowMapper;
	}

	public Iterator<T> extractData(ResultSet rs) throws SQLException {
		final AtomicLong index = new AtomicLong(0);
		final CachedRowSet delegate = new CachedRowSetImpl();
		delegate.populate(rs);
		configure(delegate);
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

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	protected void configure(ResultSet delegate) {
	}

}
