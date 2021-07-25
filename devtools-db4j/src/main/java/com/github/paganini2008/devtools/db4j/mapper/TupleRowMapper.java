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
package com.github.paganini2008.devtools.db4j.mapper;

import com.github.paganini2008.devtools.CaseFormats;
import com.github.paganini2008.devtools.collection.Tuple;
import com.github.paganini2008.devtools.db4j.JdbcType;

/**
 * 
 * TupleRowMapper
 *
 * @author Fred Feng
 * @version 1.0
 */
public class TupleRowMapper extends AbstractRowMapper<Tuple> {

	protected Tuple createObject(int columnCount) {
		return Tuple.newTuple(CaseFormats.LOWER_CAMEL);
	}

	protected void setValue(Tuple tuple, int columnIndex, String columnName, String columnDisplayName,
			JdbcType jdbcType, Object columnValue) {
		tuple.set(columnDisplayName, columnValue);
	}

}
