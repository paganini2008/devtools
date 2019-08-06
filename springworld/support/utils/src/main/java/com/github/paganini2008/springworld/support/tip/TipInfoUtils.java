package com.github.paganini2008.springworld.support.tip;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.regex.RegexUtils;
import com.github.paganini2008.springworld.support.ApplicationContextUtils;

/**
 * 
 * TipInfoUtils
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-03
 * @version 1.0
 */
public class TipInfoUtils {

	private static final String PATTERN_PLACEHOLDER = "\\{(.*)\\}";

	public static String getMessage(String represent) {
		final String match = RegexUtils.match(represent, PATTERN_PLACEHOLDER, 0, 1, 0);
		String code = null, defaultMessage = represent;
		int index = match.indexOf(':');
		if (index > 0) {
			code = match.substring(0, index);
			defaultMessage = match.substring(index + 1);
		}
		return StringUtils.isNotBlank(match) ? getMessage(code, defaultMessage) : defaultMessage;
	}

	public static String getMessage(String code, String defaultMessage) {
		if (StringUtils.isNotBlank(code)) {
			try {
				TipInfoService tipInfoService = ApplicationContextUtils.getBean(TipInfoService.class);
				return tipInfoService.getTipMsg(code, defaultMessage);
			} catch (Exception ignored) {
			}
		}
		return defaultMessage;
	}

	public static String getMessage(String code, Object[] arguments, String defaultMessage) {
		if (StringUtils.isNotBlank(code)) {
			try {
				TipInfoService tipInfoService = ApplicationContextUtils.getBean(TipInfoService.class);
				String pattern = tipInfoService.getTipMsg(code, defaultMessage);
				return StringUtils.parseText(pattern, "{", "}", arguments);
			} catch (Exception ignored) {
			}
		}
		return defaultMessage;
	}

	private TipInfoUtils() {
	}

}
