/**
* Copyright 2017-2022 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
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
 * @since 2.0.1
 */
public class CursorResultSetExtractor<T> implements ResultSetExtractor<Cursor<T>> {

	private final RowMapper<T> rowMapper;
	private final TypeHandlerRegistry typeHandlerRegistry;
	private final Observable closeable;

	CursorResultSetExtractor(RowMapper<T> rowMapper, TypeHandlerRegistry typeHandlerRegistry, Observable closeable) {
		if (rowMapper == null) {
			throw new IllegalArgumentException("RowMapper must not be null.");
		}
		this.rowMapper = rowMapper;
		this.typeHandlerRegistry = typeHandlerRegistry;
		this.closeable = closeable;
	}

	public Cursor<T> extractData(final ResultSet delegate) throws SQLException {
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
					return rowMapper.mapRow(index.incrementAndGet(), delegate, typeHandlerRegistry);
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
			public int getRownum() {
				try {
					return delegate.getRow();
				} catch (SQLException e) {
					throw new DetachedSqlException(e.getMessage(), e);
				}
			}

			@Override
			public void mark(int rownum) {
				try {
					delegate.absolute(rownum);
				} catch (SQLException e) {
					throw new DetachedSqlException(e.getMessage(), e);
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
