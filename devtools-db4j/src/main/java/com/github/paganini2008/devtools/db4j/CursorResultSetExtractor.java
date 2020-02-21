package com.github.paganini2008.devtools.db4j;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.paganini2008.devtools.Observable;
import com.github.paganini2008.devtools.db4j.mapper.RowMapper;
import com.github.paganini2008.devtools.jdbc.Cursor;
import com.github.paganini2008.devtools.jdbc.DetachedSqlException;

/**
 * CursorResultSetExtractor
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class CursorResultSetExtractor<T> implements ResultSetExtractor<Cursor<T>> {

	private final RowMapper<T> rowMapper;
	private final Observable closeable;

	CursorResultSetExtractor(RowMapper<T> rowMapper, Observable closeable) {
		if (rowMapper == null) {
			throw new IllegalArgumentException("RowMapper must not be null.");
		}
		this.rowMapper = rowMapper;
		this.closeable = closeable;
	}

	public Cursor<T> extractData(final ResultSet delegate) throws SQLException {
		configure(delegate);
		return new Cursor<T>() {

			private final AtomicInteger index = new AtomicInteger(0);
			private final AtomicBoolean opened = new AtomicBoolean(true);

			public boolean hasNext() {
				try {
					opened.set(delegate.next());
					return opened.get();
				} catch (SQLException e) {
					opened.set(false);
					throw new DetachedSqlException(e.getMessage(), e);
				} finally {
					if (!isOpened()) {
						close();
					}
				}
			}

			public T next() {
				try {
					return rowMapper.mapRow(index.incrementAndGet(), delegate);
				} catch (SQLException e) {
					opened.set(false);
					throw new DetachedSqlException(e.getMessage(), e);
				} finally {
					if (!isOpened()) {
						close();
					}
				}
			}

			@Override
			public boolean isOpened() {
				return opened.get();
			}

			@Override
			public void close() {
				closeable.notifyObservers();
			}
		};
	}

	protected void configure(ResultSet delegate) {
	}

}
