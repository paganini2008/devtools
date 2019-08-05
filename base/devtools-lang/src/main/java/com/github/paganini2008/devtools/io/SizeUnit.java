package com.github.paganini2008.devtools.io;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * SizeUnit
 * 
 * @author Fred Feng
 * @version 1.0
 */
public enum SizeUnit {

	BYTES {

		public BigDecimal toBytes(long size) {
			return BigDecimal.valueOf(size);
		}

		public BigDecimal toKB(long size) {
			return goDown(size, MB_BIG);
		}

		public BigDecimal toMB(long size) {
			return goDown(size, MB_BIG);
		}

		public BigDecimal toGB(long size) {
			return goDown(size, GB_BIG);
		}

		public BigDecimal toTB(long size) {
			return goDown(size, TB_BIG);
		}

		public BigDecimal toPB(long size) {
			return goDown(size, PB_BIG);
		}

		public BigDecimal toEB(long size) {
			return goDown(size, EB_BIG);
		}

		public BigDecimal toZB(long size) {
			return goDown(size, ZB_BIG);
		}

		public BigDecimal toYB(long size) {
			return goDown(size, YB_BIG);
		}

		public BigDecimal convert(long size, SizeUnit sizeUnit) {
			return sizeUnit.toBytes(size);
		}

	},

	KB {

		public BigDecimal toBytes(long size) {
			return goUp(size, KB_BIG);
		}

		public BigDecimal toKB(long size) {
			return BigDecimal.valueOf(size);
		}

		public BigDecimal toMB(long size) {
			return goDown(size, MB_BIG, KB_BIG);
		}

		public BigDecimal toGB(long size) {
			return goDown(size, GB_BIG, KB_BIG);
		}

		public BigDecimal toTB(long size) {
			return goDown(size, TB_BIG, KB_BIG);
		}

		public BigDecimal toPB(long size) {
			return goDown(size, PB_BIG, KB_BIG);
		}

		public BigDecimal toEB(long size) {
			return goDown(size, EB_BIG, KB_BIG);
		}

		public BigDecimal toZB(long size) {
			return goDown(size, ZB_BIG, KB_BIG);
		}

		public BigDecimal toYB(long size) {
			return goDown(size, YB_BIG, KB_BIG);
		}

		public BigDecimal convert(long size, SizeUnit sizeUnit) {
			return sizeUnit.toKB(size);
		}

	},
	MB {

		public BigDecimal toBytes(long size) {
			return goUp(size, MB_BIG);
		}

		public BigDecimal toKB(long size) {
			return goUp(size, MB_BIG, KB_BIG);
		}

		public BigDecimal toMB(long size) {
			return BigDecimal.valueOf(size);
		}

		public BigDecimal toGB(long size) {
			return goDown(size, GB_BIG, MB_BIG);
		}

		public BigDecimal toTB(long size) {
			return goDown(size, TB_BIG, MB_BIG);
		}

		public BigDecimal toPB(long size) {
			return goDown(size, PB_BIG, MB_BIG);
		}

		public BigDecimal toEB(long size) {
			return goDown(size, EB_BIG, MB_BIG);
		}

		public BigDecimal toZB(long size) {
			return goDown(size, ZB_BIG, MB_BIG);
		}

		public BigDecimal toYB(long size) {
			return goDown(size, YB_BIG, MB_BIG);
		}

		public BigDecimal convert(long size, SizeUnit sizeUnit) {
			return sizeUnit.toMB(size);
		}

	},
	GB {
		public BigDecimal toBytes(long size) {
			return goUp(size, GB_BIG);
		}

		public BigDecimal toKB(long size) {
			return goUp(size, GB_BIG, KB_BIG);
		}

		public BigDecimal toMB(long size) {
			return goUp(size, GB_BIG, MB_BIG);
		}

		public BigDecimal toGB(long size) {
			return BigDecimal.valueOf(size);
		}

		public BigDecimal toTB(long size) {
			return goDown(size, TB_BIG, GB_BIG);
		}

		public BigDecimal toPB(long size) {
			return goDown(size, PB_BIG, GB_BIG);
		}

		public BigDecimal toEB(long size) {
			return goDown(size, EB_BIG, GB_BIG);
		}

		public BigDecimal toZB(long size) {
			return goDown(size, ZB_BIG, GB_BIG);
		}

		public BigDecimal toYB(long size) {
			return goDown(size, YB_BIG, GB_BIG);
		}

		public BigDecimal convert(long size, SizeUnit sizeUnit) {
			return sizeUnit.toGB(size);
		}
	},
	TB {
		public BigDecimal toBytes(long size) {
			return goUp(size, TB_BIG);
		}

		public BigDecimal toKB(long size) {
			return goUp(size, TB_BIG, KB_BIG);
		}

		public BigDecimal toMB(long size) {
			return goUp(size, TB_BIG, MB_BIG);
		}

		public BigDecimal toGB(long size) {
			return goUp(size, TB_BIG, GB_BIG);
		}

		public BigDecimal toTB(long size) {
			return BigDecimal.valueOf(size);
		}

		public BigDecimal toPB(long size) {
			return goDown(size, PB_BIG, TB_BIG);
		}

		public BigDecimal toEB(long size) {
			return goDown(size, EB_BIG, TB_BIG);
		}

		public BigDecimal toZB(long size) {
			return goDown(size, ZB_BIG, TB_BIG);
		}

		public BigDecimal toYB(long size) {
			return goDown(size, YB_BIG, TB_BIG);
		}

		public BigDecimal convert(long size, SizeUnit sizeUnit) {
			return sizeUnit.toTB(size);
		}
	},
	PB {
		public BigDecimal toBytes(long size) {
			return goUp(size, PB_BIG);
		}

		public BigDecimal toKB(long size) {
			return goUp(size, PB_BIG, KB_BIG);
		}

		public BigDecimal toMB(long size) {
			return goUp(size, PB_BIG, MB_BIG);
		}

		public BigDecimal toGB(long size) {
			return goUp(size, PB_BIG, GB_BIG);
		}

		public BigDecimal toTB(long size) {
			return goUp(size, PB_BIG, TB_BIG);
		}

		public BigDecimal toPB(long size) {
			return BigDecimal.valueOf(size);
		}

		public BigDecimal toEB(long size) {
			return goDown(size, EB_BIG, PB_BIG);
		}

		public BigDecimal toZB(long size) {
			return goDown(size, ZB_BIG, PB_BIG);
		}

		public BigDecimal toYB(long size) {
			return goDown(size, YB_BIG, PB_BIG);
		}

		public BigDecimal convert(long size, SizeUnit sizeUnit) {
			return sizeUnit.toPB(size);
		}
	},
	EB {
		public BigDecimal toBytes(long size) {
			return goUp(size, EB_BIG);
		}

		public BigDecimal toKB(long size) {
			return goUp(size, EB_BIG, KB_BIG);
		}

		public BigDecimal toMB(long size) {
			return goUp(size, EB_BIG, MB_BIG);
		}

		public BigDecimal toGB(long size) {
			return goUp(size, EB_BIG, GB_BIG);
		}

		public BigDecimal toTB(long size) {
			return goUp(size, EB_BIG, TB_BIG);
		}

		public BigDecimal toPB(long size) {
			return goUp(size, EB_BIG, PB_BIG);
		}

		public BigDecimal toEB(long size) {
			return BigDecimal.valueOf(size);
		}

		public BigDecimal toZB(long size) {
			return goDown(size, ZB_BIG, EB_BIG);
		}

		public BigDecimal toYB(long size) {
			return goDown(size, YB_BIG, EB_BIG);
		}

		public BigDecimal convert(long size, SizeUnit sizeUnit) {
			return sizeUnit.toEB(size);
		}
	},
	ZB {
		public BigDecimal toBytes(long size) {
			return goUp(size, ZB_BIG);
		}

		public BigDecimal toKB(long size) {
			return goUp(size, ZB_BIG, KB_BIG);
		}

		public BigDecimal toMB(long size) {
			return goUp(size, ZB_BIG, MB_BIG);
		}

		public BigDecimal toGB(long size) {
			return goUp(size, ZB_BIG, GB_BIG);
		}

		public BigDecimal toTB(long size) {
			return goUp(size, ZB_BIG, TB_BIG);
		}

		public BigDecimal toPB(long size) {
			return goUp(size, ZB_BIG, PB_BIG);
		}

		public BigDecimal toEB(long size) {
			return goUp(size, ZB_BIG, EB_BIG);
		}

		public BigDecimal toZB(long size) {
			return BigDecimal.valueOf(size);
		}

		public BigDecimal toYB(long size) {
			return goDown(size, YB_BIG, ZB_BIG);
		}

		public BigDecimal convert(long size, SizeUnit sizeUnit) {
			return sizeUnit.toZB(size);
		}
	},
	YB {

		public BigDecimal toBytes(long size) {
			return goUp(size, YB_BIG);
		}

		public BigDecimal toKB(long size) {
			return goUp(size, YB_BIG, KB_BIG);
		}

		public BigDecimal toMB(long size) {
			return goUp(size, YB_BIG, MB_BIG);
		}

		public BigDecimal toGB(long size) {
			return goUp(size, YB_BIG, GB_BIG);
		}

		public BigDecimal toTB(long size) {
			return goUp(size, YB_BIG, TB_BIG);
		}

		public BigDecimal toPB(long size) {
			return goUp(size, YB_BIG, PB_BIG);
		}

		public BigDecimal toEB(long size) {
			return goUp(size, YB_BIG, EB_BIG);
		}

		public BigDecimal toZB(long size) {
			return goUp(size, YB_BIG, ZB_BIG);
		}

		public BigDecimal toYB(long size) {
			return BigDecimal.valueOf(size);
		}

		public BigDecimal convert(long size, SizeUnit sizeUnit) {
			return sizeUnit.toYB(size);
		}

	};

	static final BigDecimal KB_BIG = BigDecimal.valueOf(1024L);
	static final BigDecimal MB_BIG = KB_BIG.multiply(KB_BIG);
	static final BigDecimal GB_BIG = KB_BIG.multiply(MB_BIG);
	static final BigDecimal TB_BIG = KB_BIG.multiply(GB_BIG);
	static final BigDecimal PB_BIG = KB_BIG.multiply(TB_BIG);
	static final BigDecimal EB_BIG = KB_BIG.multiply(PB_BIG);
	static final BigDecimal ZB_BIG = KB_BIG.multiply(EB_BIG);
	static final BigDecimal YB_BIG = KB_BIG.multiply(ZB_BIG);

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
		System.out.println(SizeUnit.MB.convert(8665871 , SizeUnit.BYTES));
	}
}
