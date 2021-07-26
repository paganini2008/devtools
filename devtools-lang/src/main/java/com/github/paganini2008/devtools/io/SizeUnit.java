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
package com.github.paganini2008.devtools.io;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * SizeUnit
 * 
 * @author Fred Feng
 * @since 2.0.1
 */
public enum SizeUnit {

	BYTES {

		public BigDecimal toBytes(long size) {
			return BigDecimal.valueOf(size);
		}

		public BigDecimal toKB(long size) {
			return goDown(size, UNIT_MB);
		}

		public BigDecimal toMB(long size) {
			return goDown(size, UNIT_MB);
		}

		public BigDecimal toGB(long size) {
			return goDown(size, UNIT_GB);
		}

		public BigDecimal toTB(long size) {
			return goDown(size, UNIT_TB);
		}

		public BigDecimal toPB(long size) {
			return goDown(size, UNIT_PB);
		}

		public BigDecimal toEB(long size) {
			return goDown(size, UNIT_EB);
		}

		public BigDecimal toZB(long size) {
			return goDown(size, UNIT_ZB);
		}

		public BigDecimal toYB(long size) {
			return goDown(size, UNIT_YB);
		}

		public BigDecimal convert(long size, SizeUnit sizeUnit) {
			return sizeUnit.toBytes(size);
		}

	},

	KB {

		public BigDecimal toBytes(long size) {
			return goUp(size, UNIT_KB);
		}

		public BigDecimal toKB(long size) {
			return BigDecimal.valueOf(size);
		}

		public BigDecimal toMB(long size) {
			return goDown(size, UNIT_MB, UNIT_KB);
		}

		public BigDecimal toGB(long size) {
			return goDown(size, UNIT_GB, UNIT_KB);
		}

		public BigDecimal toTB(long size) {
			return goDown(size, UNIT_TB, UNIT_KB);
		}

		public BigDecimal toPB(long size) {
			return goDown(size, UNIT_PB, UNIT_KB);
		}

		public BigDecimal toEB(long size) {
			return goDown(size, UNIT_EB, UNIT_KB);
		}

		public BigDecimal toZB(long size) {
			return goDown(size, UNIT_ZB, UNIT_KB);
		}

		public BigDecimal toYB(long size) {
			return goDown(size, UNIT_YB, UNIT_KB);
		}

		public BigDecimal convert(long size, SizeUnit sizeUnit) {
			return sizeUnit.toKB(size);
		}

	},
	MB {

		public BigDecimal toBytes(long size) {
			return goUp(size, UNIT_MB);
		}

		public BigDecimal toKB(long size) {
			return goUp(size, UNIT_MB, UNIT_KB);
		}

		public BigDecimal toMB(long size) {
			return BigDecimal.valueOf(size);
		}

		public BigDecimal toGB(long size) {
			return goDown(size, UNIT_GB, UNIT_MB);
		}

		public BigDecimal toTB(long size) {
			return goDown(size, UNIT_TB, UNIT_MB);
		}

		public BigDecimal toPB(long size) {
			return goDown(size, UNIT_PB, UNIT_MB);
		}

		public BigDecimal toEB(long size) {
			return goDown(size, UNIT_EB, UNIT_MB);
		}

		public BigDecimal toZB(long size) {
			return goDown(size, UNIT_ZB, UNIT_MB);
		}

		public BigDecimal toYB(long size) {
			return goDown(size, UNIT_YB, UNIT_MB);
		}

		public BigDecimal convert(long size, SizeUnit sizeUnit) {
			return sizeUnit.toMB(size);
		}

	},
	GB {
		public BigDecimal toBytes(long size) {
			return goUp(size, UNIT_GB);
		}

		public BigDecimal toKB(long size) {
			return goUp(size, UNIT_GB, UNIT_KB);
		}

		public BigDecimal toMB(long size) {
			return goUp(size, UNIT_GB, UNIT_MB);
		}

		public BigDecimal toGB(long size) {
			return BigDecimal.valueOf(size);
		}

		public BigDecimal toTB(long size) {
			return goDown(size, UNIT_TB, UNIT_GB);
		}

		public BigDecimal toPB(long size) {
			return goDown(size, UNIT_PB, UNIT_GB);
		}

		public BigDecimal toEB(long size) {
			return goDown(size, UNIT_EB, UNIT_GB);
		}

		public BigDecimal toZB(long size) {
			return goDown(size, UNIT_ZB, UNIT_GB);
		}

		public BigDecimal toYB(long size) {
			return goDown(size, UNIT_YB, UNIT_GB);
		}

		public BigDecimal convert(long size, SizeUnit sizeUnit) {
			return sizeUnit.toGB(size);
		}
	},
	TB {
		public BigDecimal toBytes(long size) {
			return goUp(size, UNIT_TB);
		}

		public BigDecimal toKB(long size) {
			return goUp(size, UNIT_TB, UNIT_KB);
		}

		public BigDecimal toMB(long size) {
			return goUp(size, UNIT_TB, UNIT_MB);
		}

		public BigDecimal toGB(long size) {
			return goUp(size, UNIT_TB, UNIT_GB);
		}

		public BigDecimal toTB(long size) {
			return BigDecimal.valueOf(size);
		}

		public BigDecimal toPB(long size) {
			return goDown(size, UNIT_PB, UNIT_TB);
		}

		public BigDecimal toEB(long size) {
			return goDown(size, UNIT_EB, UNIT_TB);
		}

		public BigDecimal toZB(long size) {
			return goDown(size, UNIT_ZB, UNIT_TB);
		}

		public BigDecimal toYB(long size) {
			return goDown(size, UNIT_YB, UNIT_TB);
		}

		public BigDecimal convert(long size, SizeUnit sizeUnit) {
			return sizeUnit.toTB(size);
		}
	},
	PB {
		public BigDecimal toBytes(long size) {
			return goUp(size, UNIT_PB);
		}

		public BigDecimal toKB(long size) {
			return goUp(size, UNIT_PB, UNIT_KB);
		}

		public BigDecimal toMB(long size) {
			return goUp(size, UNIT_PB, UNIT_MB);
		}

		public BigDecimal toGB(long size) {
			return goUp(size, UNIT_PB, UNIT_GB);
		}

		public BigDecimal toTB(long size) {
			return goUp(size, UNIT_PB, UNIT_TB);
		}

		public BigDecimal toPB(long size) {
			return BigDecimal.valueOf(size);
		}

		public BigDecimal toEB(long size) {
			return goDown(size, UNIT_EB, UNIT_PB);
		}

		public BigDecimal toZB(long size) {
			return goDown(size, UNIT_ZB, UNIT_PB);
		}

		public BigDecimal toYB(long size) {
			return goDown(size, UNIT_YB, UNIT_PB);
		}

		public BigDecimal convert(long size, SizeUnit sizeUnit) {
			return sizeUnit.toPB(size);
		}
	},
	EB {
		public BigDecimal toBytes(long size) {
			return goUp(size, UNIT_EB);
		}

		public BigDecimal toKB(long size) {
			return goUp(size, UNIT_EB, UNIT_KB);
		}

		public BigDecimal toMB(long size) {
			return goUp(size, UNIT_EB, UNIT_MB);
		}

		public BigDecimal toGB(long size) {
			return goUp(size, UNIT_EB, UNIT_GB);
		}

		public BigDecimal toTB(long size) {
			return goUp(size, UNIT_EB, UNIT_TB);
		}

		public BigDecimal toPB(long size) {
			return goUp(size, UNIT_EB, UNIT_PB);
		}

		public BigDecimal toEB(long size) {
			return BigDecimal.valueOf(size);
		}

		public BigDecimal toZB(long size) {
			return goDown(size, UNIT_ZB, UNIT_EB);
		}

		public BigDecimal toYB(long size) {
			return goDown(size, UNIT_YB, UNIT_EB);
		}

		public BigDecimal convert(long size, SizeUnit sizeUnit) {
			return sizeUnit.toEB(size);
		}
	},
	ZB {
		public BigDecimal toBytes(long size) {
			return goUp(size, UNIT_ZB);
		}

		public BigDecimal toKB(long size) {
			return goUp(size, UNIT_ZB, UNIT_KB);
		}

		public BigDecimal toMB(long size) {
			return goUp(size, UNIT_ZB, UNIT_MB);
		}

		public BigDecimal toGB(long size) {
			return goUp(size, UNIT_ZB, UNIT_GB);
		}

		public BigDecimal toTB(long size) {
			return goUp(size, UNIT_ZB, UNIT_TB);
		}

		public BigDecimal toPB(long size) {
			return goUp(size, UNIT_ZB, UNIT_PB);
		}

		public BigDecimal toEB(long size) {
			return goUp(size, UNIT_ZB, UNIT_EB);
		}

		public BigDecimal toZB(long size) {
			return BigDecimal.valueOf(size);
		}

		public BigDecimal toYB(long size) {
			return goDown(size, UNIT_YB, UNIT_ZB);
		}

		public BigDecimal convert(long size, SizeUnit sizeUnit) {
			return sizeUnit.toZB(size);
		}
	},
	YB {

		public BigDecimal toBytes(long size) {
			return goUp(size, UNIT_YB);
		}

		public BigDecimal toKB(long size) {
			return goUp(size, UNIT_YB, UNIT_KB);
		}

		public BigDecimal toMB(long size) {
			return goUp(size, UNIT_YB, UNIT_MB);
		}

		public BigDecimal toGB(long size) {
			return goUp(size, UNIT_YB, UNIT_GB);
		}

		public BigDecimal toTB(long size) {
			return goUp(size, UNIT_YB, UNIT_TB);
		}

		public BigDecimal toPB(long size) {
			return goUp(size, UNIT_YB, UNIT_PB);
		}

		public BigDecimal toEB(long size) {
			return goUp(size, UNIT_YB, UNIT_EB);
		}

		public BigDecimal toZB(long size) {
			return goUp(size, UNIT_YB, UNIT_ZB);
		}

		public BigDecimal toYB(long size) {
			return BigDecimal.valueOf(size);
		}

		public BigDecimal convert(long size, SizeUnit sizeUnit) {
			return sizeUnit.toYB(size);
		}

	};

	static final BigDecimal UNIT_KB = BigDecimal.valueOf(1024L);
	static final BigDecimal UNIT_MB = UNIT_KB.multiply(UNIT_KB);
	static final BigDecimal UNIT_GB = UNIT_KB.multiply(UNIT_MB);
	static final BigDecimal UNIT_TB = UNIT_KB.multiply(UNIT_GB);
	static final BigDecimal UNIT_PB = UNIT_KB.multiply(UNIT_TB);
	static final BigDecimal UNIT_EB = UNIT_KB.multiply(UNIT_PB);
	static final BigDecimal UNIT_ZB = UNIT_KB.multiply(UNIT_EB);
	static final BigDecimal UNIT_YB = UNIT_KB.multiply(UNIT_ZB);

	private static BigDecimal goUp(long size, BigDecimal left, BigDecimal right) {
		return BigDecimal.valueOf(size).multiply(left.divide(right));
	}

	private static BigDecimal goUp(long size, BigDecimal left) {
		return BigDecimal.valueOf(size).multiply(left);
	}

	private static BigDecimal goDown(long size, BigDecimal left, BigDecimal right) {
		BigDecimal val = left.divide(right);
		return BigDecimal.valueOf(size).divide(val, 2, RoundingMode.HALF_UP);
	}

	private static BigDecimal goDown(long size, BigDecimal left) {
		return BigDecimal.valueOf(size).divide(left, 2, RoundingMode.HALF_UP);
	}

	public abstract BigDecimal toBytes(long size);

	public abstract BigDecimal toKB(long size);

	public abstract BigDecimal toMB(long size);

	public abstract BigDecimal toGB(long size);

	public abstract BigDecimal toTB(long size);

	public abstract BigDecimal toPB(long size);

	public abstract BigDecimal toEB(long size);

	public abstract BigDecimal toZB(long size);

	public abstract BigDecimal toYB(long size);

	public abstract BigDecimal convert(long size, SizeUnit sizeUnit);

	public static void main(String[] args) {
		System.out.println(SizeUnit.MB.convert(8665871, SizeUnit.BYTES));
	}
}
