package com.github.paganini2008.springworld.webcrawler.utils;

import java.util.concurrent.ThreadLocalRandom;

import org.springframework.util.DigestUtils;

/**
 * 
 * IpUtils
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
public abstract class IpUtils {

	private static final int[][] range = { { 607649792, 608174079 }, { 1038614528, 1039007743 }, { 1783627776, 1784676351 },
			{ 2035023872, 2035154943 }, { 2078801920, 2079064063 }, { -1950089216, -1948778497 }, { -1425539072, -1425014785 },
			{ -1236271104, -1235419137 }, { -770113536, -768606209 }, { -569376768, -564133889 }, };

	public static String getRandomIp() {
		int index = ThreadLocalRandom.current().nextInt(range.length);
		int ip = range[index][0] + ThreadLocalRandom.current().nextInt(range[index][1] - range[index][0]);
		int[] b = new int[4];
		b[0] = (int) ((ip >> 24) & 0xff);
		b[1] = (int) ((ip >> 16) & 0xff);
		b[2] = (int) ((ip >> 8) & 0xff);
		b[3] = (int) (ip & 0xff);
		return Integer.toString(b[0]) + "." + Integer.toString(b[1]) + "." + Integer.toString(b[2]) + "." + Integer.toString(b[3]);
	}

	public static void main(String[] args) {
//		for (int i = 0; i < 10000; i++) {
//			System.out.println(getRandomIp());
//		}
		System.out.println(DigestUtils.md5DigestAsHex("fengyan".getBytes()));
		System.out.println(DigestUtils.md5DigestAsHex("fengyan".getBytes()));
	}

}
