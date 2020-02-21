package com.github.paganini2008.devtools.db4j;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.rowset.CachedRowSet;

import com.github.paganini2008.devtools.db4j.mapper.RowMapper;
import com.github.paganini2008.devtools.jdbc.Cursor;
import com.github.paganini2008.devtools.jdbc.DetachedSqlException;
import com.github.paganini2008.devtools.jdbc.JdbcUtils;
import com.sun.rowset.CachedRowSetImpl;

/**
 * CachedCursorResultSetExtractor
 * 
 * @author Fred Feng
 * @version 1.0
 */
@SuppressWarnings("restriction")
public class CachedCursorResultSetExtractor<T> implements ResultSetExtractor<Cursor<T>> {

	private final RowMapper<T> rowMapper;

	CachedCursorResultSetExtractor(RowMapper<T> rowMapper) {
		if (rowMapper == null) {
			throw new IllegalArgumentException("RowMapper must not be null.");
		}
		this.rowMapper = rowMapper;
	}

	public Cursor<T> extractData(ResultSet rs) throws SQLException {
		final CachedRowSet delegate = new CachedRowSetImpl();
		delegate.populate(rs);
		configure(delegate);
		return new Cursor<T>() {

			private final AtomicInteger index = new AtomicInteger(0);
			private final AtomicBoolean opened = new AtomicBoolean(true);

			@Override
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

			@Override
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
				JdbcUtils.closeQuietly(delegate);
			}

		};
	}

	protected void configure(ResultSet delegate) {
	}

}
