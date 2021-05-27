package com.github.paganini2008.devtools.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * EnumUtils
 *
 * @author Fred Feng
 * @version 1.0
 */
public abstract class EnumUtils {

	@SuppressWarnings("unchecked")
	public static <T extends EnumConstant> T[] findAll(Class<T> enumType, String group) {
		List<T> matches = new ArrayList<T>();
		for (T constant : enumType.getEnumConstants()) {
			if (constant.getGroup().equals(group)) {
				matches.add(constant);
			}
		}
		return (T[]) matches.toArray();
	}

	public static <T extends EnumConstant> T valueOf(Class<T> enumType, int ordinal) {
		for (T constant : enumType.getEnumConstants()) {
			if (ordinal == constant.getValue()) {
				return constant;
			}
		}
		throw new IllegalArgumentException("No enum  by ordinal '" + ordinal + "' of " + enumType.getCanonicalName());
	}

}
