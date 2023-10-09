package com.gci.schedule.driverless.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
	private static final char SEPARATOR = '_';
	
	private static final String METCH_PATTERN_REGEX="[\\*]+";
	private static final String METCH_PATTERN="*";
	private static final String REPLACEMENT="[\\\\S|\\\\s]*";
	public static final String BLANK="";

	public static final Map<String, String> GENERATE_MSG = new HashMap<>();

	public static String MSG_KEY = null;

	public static String getMsgKey(){
		MSG_KEY = String.valueOf(UUID.randomUUID());
		return MSG_KEY;
	}

	public static void putGenerateMsg(){
		if(Objects.nonNull(MSG_KEY)){
			GENERATE_MSG.put(MSG_KEY,"生成成功，获取大数据站间用时失败");
		}
	}

	/**
	 * 检测字符串是否为空
	 * 
	 * @param str
	 * @return 不为空 true 为空false
	 * */
	public static boolean checkBlankNull(String str) {
		synchronized (EncodeUtil.class) {
			return (null != str && !"".equals(str) && !"null".equals(str));
		}
	}

	public static boolean isNotEmpty(String s) {
		return !isEmpty(s);
	}

	public static boolean isEmpty(String s) {
		if (isNull(s)) {
			return true;
		}
		return s.trim().length() < 1 ? true : false;
	}

	public static boolean isNotNull(Object obj) {
		return obj != null ? true : false;
	}

	public static boolean isNull(Object obj) {
		return obj == null ? true : false;
	}

	/**
	 * 简单将key转换成小写，且将下划线去掉、下划线后面的第一个字母改成大写
	 * 
	 * @param key
	 * @return
	 */
	public static String toCamelCase(String key) {
		String keyStr = key.toLowerCase();
		StringBuilder sb = new StringBuilder(keyStr.length());
		boolean upperCase = false;
		for (int i = 0; i < keyStr.length(); i++) {
			char c = keyStr.charAt(i);
			if (c == SEPARATOR) {
				upperCase = true;
			} else if (upperCase) {
				sb.append(Character.toUpperCase(c));
				upperCase = false;
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	/**
	 * 简单将key的下划线去掉，并将下划线后面的第一个字母改成大写
	 * 
	 * @param key
	 * @return
	 */
	public static String toCamelOldCase(String key) {
		StringBuilder sb = new StringBuilder(key.length());
		boolean upperCase = false;
		for (int i = 0; i < key.length(); i++) {
			char c = key.charAt(i);
			if (c == SEPARATOR) {
				upperCase = true;
			} else if (upperCase) {
				sb.append(Character.toUpperCase(c));
				upperCase = false;
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}
	
	/**
     * <p>Checks if a String is not empty (""), not null and not whitespace only.</p>
     *
     * <pre>
     * StringUtils.isNotBlank(null)      = false
     * StringUtils.isNotBlank("")        = false
     * StringUtils.isNotBlank(" ")       = false
     * StringUtils.isNotBlank("bob")     = true
     * StringUtils.isNotBlank("  bob  ") = true
     * </pre>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if the String is
     *  not empty and not null and not whitespace
     * @since 2.0
     */
    public static boolean isNotBlank(final String string) {
        return !isBlank(string);
    }
    
    /**
     * <p>Checks if a String is whitespace, empty ("") or null.</p>
     *
     * <pre>
     * StringUtils.isBlank(null)      = true
     * StringUtils.isBlank("")        = true
     * StringUtils.isBlank(" ")       = true
     * StringUtils.isBlank("bob")     = false
     * StringUtils.isBlank("  bob  ") = false
     * </pre>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if the String is null, empty or whitespace
     * @since 2.0
     */
	public static boolean isBlank(final String string) {
        int strLen;
        if (string == null || (strLen = string.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(string.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }
	
	/**
	 * when string is null return blank string
	 * @param string
	 * @return String
	 */
	public static String nullToBlank(final String string){
		return string==null?BLANK:string;
	}
	
	/**
	 * <p>Method:only for '*' match pattern,return true of false</p>
	 * @param string
	 * @param patternString
	 * @return boolean
	 */
	public static boolean isMatchPattern(final String string, final String patternString) {
		boolean result=false;
		if(string!=null&&patternString!=null){
			if(patternString.indexOf(METCH_PATTERN)>=0){
				String matchPattern=patternString.replaceAll(METCH_PATTERN_REGEX, REPLACEMENT);
				Pattern pattern = Pattern.compile(matchPattern);
				Matcher matcher = pattern.matcher(string);
				result=matcher.find();
			}else{
				if(string.equals(patternString)){
					result=true;
				}
			}
		}
		return result;
	}
}
