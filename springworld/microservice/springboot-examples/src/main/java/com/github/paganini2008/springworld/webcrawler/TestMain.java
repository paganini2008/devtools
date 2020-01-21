package com.github.paganini2008.springworld.webcrawler;

import java.io.IOException;

import org.apache.commons.codec.cli.Digest;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import com.github.paganini2008.devtools.CharsetUtils;
import com.github.paganini2008.devtools.io.FileUtils;

public class TestMain {

	public static boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return true;
		}
		return false;
	}

	public static String decodeUnicode(final String unicode) {
		StringBuffer string = new StringBuffer();

		String[] hex = unicode.split("\\\\u");

		for (int i = 0; i < hex.length; i++) {

			try {
				// 汉字范围 \u4e00-\u9fa5 (中文)
				if (hex[i].length() >= 4) {// 取前四个，判断是否是汉字
					String chinese = hex[i].substring(0, 4);
					try {
						int chr = Integer.parseInt(chinese, 16);
						boolean isChinese = isChinese((char) chr);
						// 转化成功，判断是否在 汉字范围内
						if (isChinese) {// 在汉字范围内
							// 追加成string
							string.append((char) chr);
							// 并且追加 后面的字符
							String behindString = hex[i].substring(4);
							string.append(behindString);
						} else {
							string.append(hex[i]);
						}
					} catch (NumberFormatException e1) {
						string.append(hex[i]);
					}

				} else {
					string.append(hex[i]);
				}
			} catch (NumberFormatException e) {
				string.append(hex[i]);
			}
		}

		return string.toString();
	}

	public static void main(String[] args) throws IOException {
		//String unicode = FileUtils.toString("d:/json.txt", CharsetUtils.UTF_8);
		//System.out.println(decodeUnicode(unicode));
		PathMatcher pathMatcher = new AntPathMatcher();
		System.out.println(pathMatcher.match("http://*.haochi123.com/", "http://caipu.haochi123.com/"));
	}

}
