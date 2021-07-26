/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

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
package com.github.paganini2008.devtools.jdbc;

import java.util.concurrent.Executor;
import java.util.function.Predicate;

import com.github.paganini2008.devtools.collection.Tuple;

/**
 * 
 * JdbcDumpOptions
 *
 * @author Fred Feng
 *
 * @since 2.0.2
 */
public interface JdbcDumpOptions {

	default String getCatalog() {
		return null;
	}

	default String getSchema() {
		return null;
	}

	default String getTableName() {
		return null;
	}

	default Executor getExecutor() {
		return null;
	}

	default long getMaxRecords() {
		return -1L;
	}

	String getInsertionSql(Tuple t);

	default Object[] getArgs(Tuple t) {
		return t != null ? t.toValues() : new Object[0];
	}

	default Predicate<Tuple> getPredicate() {
		return t -> true;
	}

}
