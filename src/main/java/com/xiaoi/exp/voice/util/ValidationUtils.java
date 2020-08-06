package com.xiaoi.exp.voice.util;


/**
 * 验证工具类
 * 
 * @author Yale
 * 
 */
public class ValidationUtils {
	/**
	 * 验证对象是否为空
	 * 
	 * @param obj
	 *            需要验证的对象
	 * @return 若为空返回true,否则返回false
	 */
	public static boolean isEmpty(Object obj) {
		return obj == null || "".equals(obj.toString().trim())||"null".equals(obj.toString().trim());
	}

	/**
	 * 验证对象是否不为空或者空字符串
	 * 
	 * @param object
	 *            需要验证的对象
	 * @return 不为空返回true, 否则返回false
	 */
	public static boolean isNotEmpty(Object object) {
		return !isEmpty(object);
	}
}
