/**
* Copyright 2018-2021 Fred Feng (paganini.fy@gmail.com)

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
package com.github.paganini2008.devtools.io.filter;

import java.io.File;

/**
 * FileSizeFileFilter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class FileSizeFileFilter extends LogicalFileFilter {

	private final int size;
	private final Operator operator;

	public FileSizeFileFilter(int size, Operator operator) {
		this.size = size;
		this.operator = operator;
	}

	public boolean accept(File file) {
		String[] names = file.list();
		final int l = names != null ? names.length : 0;
		switch (operator) {
		case LT:
			return l < size;
		case GT:
			return l > size;
		case LTE:
			return l <= size;
		case GTE:
			return l >= size;
		case EQ:
			return l == size;
		case NE:
			return l != size;
		}
		throw new UnsupportedOperationException();
	}
	
	public static FileSizeFileFilter eq(int size) {
		return new FileSizeFileFilter(size, Operator.EQ);
	}

	public static FileSizeFileFilter ne(int size) {
		return new FileSizeFileFilter(size, Operator.NE);
	}

	public static FileSizeFileFilter gte(int size) {
		return new FileSizeFileFilter(size, Operator.GTE);
	}

	public static FileSizeFileFilter gt(int size) {
		return new FileSizeFileFilter(size, Operator.GT);
	}

	public static FileSizeFileFilter lte(int size) {
		return new FileSizeFileFilter(size, Operator.LTE);
	}

	public static FileSizeFileFilter lt(int size) {
		return new FileSizeFileFilter(size, Operator.LT);
	}

}
