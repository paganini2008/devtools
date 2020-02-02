package com.github.paganini2008.devtools.db4j;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.paganini2008.devtools.Observable;
import com.github.paganini2008.devtools.db4j.mapper.RowMapper;

/**
 * IteratorResultSetExtractor
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class IteratorResultSetExtractor<T> implements ResultSetExtractor<Iterator<T>> {

	private final RowMapper<T> rowMapper;
	private final Observable closeable;

	IteratorResultSetExtractor(RowMapper<T> rowMapper, Observable closeable) {
		if (rowMapper == null) {
			throw new IllegalArgumentException("RowMapper must not be null.");
		}
		this.rowMapper = rowMapper;
		this.closeable = closeable;
	}

	public Iterator<T> extractData(final ResultSet delegate) throws SQLException {
		configure(delegate);
		final AtomicInteger index = new AtomicInteger(0);
		return new Iterator<T>() {

			public boolean hasNext() {
				boolean state = false;
				try {
					return (state = delegate.next());
				} catch (SQLException e) {
					throw new IllegalStateException(e.getMessage(), e);
				} finally {
					if (!state) {
						closeable.notifyObservers();
					}
				}
			}

			public T next() {
				boolean state = true;
				try {
					return rowMapper.mapRow(index.incrementAndGet(), delegate);
				} catch (SQLException e) {
					state = false;
					throw new IllegalStateException(e.getMessage(), e);
				} finally {
					if (!state) {
						closeable.notifyObservers();
					}
				}
			}
		};
	}

	protected void configure(ResultSet delegate) {
	}

}
