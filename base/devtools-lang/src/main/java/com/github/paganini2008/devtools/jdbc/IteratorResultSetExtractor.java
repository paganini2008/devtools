package com.github.paganini2008.devtools.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLong;

import com.github.paganini2008.devtools.jdbc.SqlRunner.LazyCloseable;
import com.github.paganini2008.devtools.jdbc.mapper.RowMapper;

/**
 * IteratorResultSetExtractor
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class IteratorResultSetExtractor<T> implements ResultSetExtractor<Iterator<T>> {

	private final RowMapper<T> rowMapper;
	private final LazyCloseable closeable;

	IteratorResultSetExtractor(RowMapper<T> rowMapper, LazyCloseable closeable) {
		if (rowMapper == null) {
			throw new IllegalArgumentException("RowMapper must not be null.");
		}
		this.rowMapper = rowMapper;
		this.closeable = closeable;
	}

	public Iterator<T> extractData(final ResultSet delegate) throws SQLException {
		configure(delegate);
		final AtomicLong index = new AtomicLong(0);
		return new Iterator<T>() {

			public boolean hasNext() {
				boolean next = false;
				try {
					return (next = delegate.next());
				} catch (SQLException e) {
					throw new IllegalStateException(e.getMessage(), e);
				} finally {
					if (!next) {
						closeable.notifyObservers();
					}
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
