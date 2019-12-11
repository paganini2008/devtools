package com.github.paganini2008.springworld.redisplus;

public class TestMain {
	
	private static boolean matchesChannel(String keyPattern, String channel) {
		String key;
		int index = keyPattern.lastIndexOf('*');
		if (index == keyPattern.length() - 1) {
			key = keyPattern.substring(0, index);
			return channel.startsWith(key);
		} else if (index == 0) {
			key = keyPattern.substring(1);
			return channel.endsWith(key);
		} else {
			String[] args = keyPattern.split("\\*");
			for (String arg : args) {
				if (!channel.contains(arg)) {
					return false;
				}
			}
			return true;
		}
	}

	public static void main(String[] args) {
		String keyPattern = "abc*jk*lh";
		boolean result = matchesChannel(keyPattern, "abcnmkljklh");
		System.out.println(result);
	}

}
