package com.github.paganini2008.devtools;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import com.github.paganini2008.devtools.collection.CollectionUtils;
import com.github.paganini2008.devtools.collection.MapUtils;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;
import com.github.paganini2008.devtools.primitives.Booleans;
import com.github.paganini2008.devtools.primitives.Bytes;
import com.github.paganini2008.devtools.primitives.Chars;
import com.github.paganini2008.devtools.primitives.Doubles;
import com.github.paganini2008.devtools.primitives.Floats;
import com.github.paganini2008.devtools.primitives.Ints;
import com.github.paganini2008.devtools.primitives.Longs;
import com.github.paganini2008.devtools.primitives.Shorts;

/**
 * ObjectUtils
 * 
 * @author Fred Feng
 * @version 1.0
 */
public abstract class ObjectUtils {

	public static String toString(Object other) {
		return other != null ? other.toString().trim() : "";
	}

	public static int hashCode(Object other) {
		return other != null ? other.hashCode() : 0;
	}

	public static boolean notEquals(Object left, Object right) {
		return !equals(left, right);
	}

	public static boolean equals(Object left, Object right) {
		return left == null ? right == null : left.equals(right);
	}

	public static boolean isArray(Object obj) {
		return obj != null ? obj.getClass().isArray() : false;
	}

	public static boolean isNotArray(Object obj) {
		return !isArray(obj);
	}

	public static boolean isInstance(Object arg, Class<?> c) {
		return arg != null ? c.isInstance(arg) : false;
	}

	public static boolean isNotInstance(Object arg, Class<?> c) {
		return !isInstance(arg, c);
	}

	public static boolean isNotSameType(Object left, Object right) {
		return !isSameType(left, right);
	}

	public static boolean isSameType(Object left, Object right) {
		if (left != null && right != null) {
			return right.getClass() == left.getClass();
		}
		return left == right;
	}

	public static String deepToString(Object obj) {
		if (obj == null) {
			return "";
		}
		String str;
		if (obj instanceof Object[]) {
			str = ArrayUtils.toString((Object[]) obj);
		} else if (obj instanceof byte[]) {
			str = Bytes.toString((byte[]) obj);
		} else if (obj instanceof char[]) {
			str = Chars.toString((char[]) obj);
		} else if (obj instanceof short[]) {
			str = Shorts.toString((short[]) obj);
		} else if (obj instanceof int[]) {
			str = Ints.toString((int[]) obj);
		} else if (obj instanceof long[]) {
			str = Longs.toString((long[]) obj);
		} else if (obj instanceof float[]) {
			str = Floats.toString((float[]) obj);
		} else if (obj instanceof double[]) {
			str = Doubles.toString((double[]) obj);
		} else if (CollectionUtils.isCollection(obj)) {
			str = CollectionUtils.toString((Collection<?>) obj);
		} else if (MapUtils.isMap(obj)) {
			str = MapUtils.toString((Map<?, ?>) obj);
		} else if (CollectionUtils.isIterator(obj)) {
			str = CollectionUtils.toString((Iterator<?>) obj);
		} else if (CollectionUtils.isEnumeration(obj)) {
			str = CollectionUtils.toString((Enumeration<?>) obj);
		} else {
			str = obj.toString();
		}
		return str;
	}

	@SuppressWarnings("all")
	public static boolean deepEquals(Object left, Object right) {
		if (left == right) {
			return true;
		}
		if (left == null) {
			return right == null;
		} else if (right == null) {
			return false;
		}
		if (left instanceof Object[] && right instanceof Object[]) {
			return ArrayUtils.deepEquals((Object[]) left, (Object[]) right);
		} else if (left instanceof boolean[] && right instanceof boolean[]) {
			return Booleans.deepEquals((boolean[]) left, (boolean[]) right);
		} else if (left instanceof byte[] && right instanceof byte[]) {
			return Bytes.deepEquals((byte[]) left, (byte[]) right);
		} else if (left instanceof char[] && right instanceof char[]) {
			return Chars.deepEquals((char[]) left, (char[]) right);
		} else if (left instanceof short[] && right instanceof short[]) {
			return Shorts.deepEquals((short[]) left, (short[]) right);
		} else if (left instanceof int[] && right instanceof int[]) {
			return Ints.deepEquals((int[]) left, (int[]) right);
		} else if (left instanceof long[] && right instanceof long[]) {
			return Longs.deepEquals((long[]) left, (long[]) right);
		} else if (left instanceof float[] && right instanceof float[]) {
			return Floats.deepEquals((float[]) left, (float[]) right);
		} else if (left instanceof double[] && right instanceof double[]) {
			return Doubles.deepEquals((double[]) left, (double[]) right);
		} else if (left instanceof Collection && right instanceof Collection) {
			return CollectionUtils.deepEquals((Collection<?>) left, (Collection<?>) right);
		} else if (left instanceof Map && right instanceof Map) {
			return MapUtils.deepEquals((Map) left, (Map) right);
		} else if (left instanceof Iterator && right instanceof Iterator) {
			return CollectionUtils.deepEquals((Iterator<?>) left, (Iterator<?>) right);
		} else if (left instanceof Enumeration && right instanceof Enumeration) {
			return CollectionUtils.deepEquals((Enumeration<?>) left, (Enumeration<?>) right);
		} else {
			return left.equals(right);
		}
	}

	public static int deepHashCode(Object result) {
		if (result == null) {
			return 0;
		}
		int hash;
		if (result instanceof Byte) {
			hash = Bytes.hashCode((Byte) result);
		} else if (result instanceof Character) {
			hash = Chars.hashCode((Character) result);
		} else if (result instanceof Short) {
			hash = Shorts.hashCode((Short) result);
		} else if (result instanceof Integer) {
			hash = Ints.hashCode((Integer) result);
		} else if (result instanceof Float) {
			hash = Floats.hashCode((Float) result);
		} else if (result instanceof Double) {
			hash = Doubles.hashCode((Double) result);
		} else if (result instanceof Long) {
			hash = Longs.hashCode((Long) result);
		} else if (result instanceof Boolean) {
			hash = Booleans.hashCode((Boolean) result);
		} else {
			if (result instanceof Object[]) {
				hash = ArrayUtils.deepHashCode((Object[]) result);
			} else if (result instanceof boolean[]) {
				hash = Booleans.deepHashCode((boolean[]) result);
			} else if (result instanceof byte[]) {
				hash = Bytes.deepHashCode((byte[]) result);
			} else if (result instanceof char[]) {
				hash = Chars.deepHashCode((char[]) result);
			} else if (result instanceof short[]) {
				hash = Shorts.deepHashCode((short[]) result);
			} else if (result instanceof int[]) {
				hash = Ints.deepHashCode((int[]) result);
			} else if (result instanceof long[]) {
				hash = Longs.deepHashCode((long[]) result);
			} else if (result instanceof float[]) {
				hash = Floats.deepHashCode((float[]) result);
			} else if (result instanceof double[]) {
				hash = Doubles.deepHashCode((double[]) result);
			} else if (result instanceof Iterable) {
				hash = CollectionUtils.deepHashCode((Iterable<?>) result);
			} else if (result instanceof Map) {
				hash = MapUtils.deepHashCode((Map<?, ?>) result);
			} else if (result instanceof Iterator) {
				hash = CollectionUtils.deepHashCode((Iterator<?>) result);
			} else if (result instanceof Enumeration) {
				hash = CollectionUtils.deepHashCode((Enumeration<?>) result);
			} else {
				hash = result.hashCode();
			}
		}
		return hash;
	}

	public static boolean accept(Acceptable acceptable) {
		int retrying = 0;
		boolean success = false;
		do {
			try {
				success = acceptable.accept();
			} catch (Throwable e) {
				acceptable.exceptionCaught(e, retrying);
			}
			ThreadUtils.sleep(acceptable.retryingInterval());
		} while (!success && retrying++ < acceptable.retries());
		return success;
	}

	public static <T> T obtain(Obtainable<T> obtainable) {
		int retrying = 0;
		do {
			try {
				return obtainable.obtain();
			} catch (Throwable e) {
				obtainable.exceptionCaught(e, retrying);
			}
			ThreadUtils.sleep(obtainable.retryingInterval());
		} while (retrying++ < obtainable.retries());
		return obtainable.defaultValue();
	}
}
