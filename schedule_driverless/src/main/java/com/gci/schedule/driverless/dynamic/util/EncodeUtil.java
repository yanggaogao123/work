package com.gci.schedule.driverless.dynamic.util;

import java.io.UnsupportedEncodingException;

public class EncodeUtil {
	private final static String UNICODE = "ISO-8859-1";
	private final static String UTF8 = "UTF-8";

	public static String unicode2Utf8(String str) {
		synchronized (EncodeUtil.class) {
			return enCode(str, UNICODE, UTF8);
		}
	}
	
	public static String getUrlString(String str) {
		synchronized (EncodeUtil.class) {
			return urlDecode(str, UNICODE);
		}
	}
	
	public static String urlDecode(String str, String dest) {
		synchronized (EncodeUtil.class) {
			String result = null;
			try {
				result = java.net.URLDecoder.decode(str, UNICODE);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			};
			return result;
		}
	}

	private static String enCode(String str, String from, String dest) {
		synchronized (EncodeUtil.class) {
			String result = null;

			try {
				result = new String(str.getBytes(from), dest);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return result;
		}
	}
	
	/**
	 * 半角转全角
	 * 
	 * @param input String.
	 *            
	 * @return 全角字符串.
	 */
	public static String ToSBC(String input) {
		char c[] = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == ' ') {
				c[i] = '\u3000';
			} else if (c[i] < '\177') {
				c[i] = (char) (c[i] + 65248);

			}
		}
		return new String(c);
	}

	/**
	 * 全角转半角
	 * 
	 * @param input String.
	 *            
	 * @return 半角字符串
	 */
	public static String ToDBC(String input) {
		char c[] = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == '\u3000') {
				c[i] = ' ';
			} else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
				c[i] = (char) (c[i] - 65248);

			}
		}
		String returnString = new String(c);
		return returnString;
	}
}
