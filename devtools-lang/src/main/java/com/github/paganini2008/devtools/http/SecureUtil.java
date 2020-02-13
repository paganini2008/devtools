/**
 * File: SecureUtil.java
 * <p>
 * Purpose:
 *
 * @author xuji
 * @version 1.0 2007-6-16
 * <p>
 * Copyright (C) 2004, 2008, HC, Inc.
 */

package com.github.paganini2008.devtools.http;


import java.security.InvalidParameterException;
import java.security.MessageDigest;


/**
 * 加密相关函数。用到bouncycastle
 */
public class SecureUtil {
    public static String md5(String text) throws Exception {
        return md5(text.getBytes("UTF-8"));
    }

    public static String md5(byte[] source) throws Exception {
        int bufferSize = 4096;
        byte[] buffer = new byte[4096];

        MessageDigest md5 = MessageDigest.getInstance("MD5");

        int remain = source.length;

        while (remain > 0) {
            int len = (remain > bufferSize) ? bufferSize : remain;
            System.arraycopy(source, source.length - remain, buffer, 0, len);
            remain = remain - len;

            md5.update(buffer, 0, len);
        }

        return byte2Hex(md5.digest());
    }

    public static String byte2Hex(byte[] bytes) throws Exception {
        final String HEX = "0123456789abcdef";

        String result = "";
        for (int i = 0; i < bytes.length; i++) {
            result += HEX.charAt(bytes[i] >> 4 & 0x0F);
            result += HEX.charAt(bytes[i] & 0x0F);
        }

        return new String(result);
    }

    public static byte[] hex2Byte(String text) throws Exception {
        final String HEX = "0123456789abcdef";

        String hexText = text;
        if (text.length() % 2 == 1) {
            hexText = "0" + text;
        }
        hexText = hexText.toLowerCase();

        int len = hexText.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++) {
            String s1 = hexText.substring(2 * i, 2 * i + 1);
            String s2 = hexText.substring(2 * i + 1, 2 * i + 2);

            int h = HEX.indexOf(s1);
            int l = HEX.indexOf(s2);

            if ((h == -1) || (l == -1)) {
                throw new InvalidParameterException();
            }

            result[i] = (byte) ((h << 4) | (l & 0x0F));
        }

        return result;
    }
}

