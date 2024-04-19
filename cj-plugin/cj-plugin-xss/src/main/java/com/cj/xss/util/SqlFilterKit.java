package com.cj.xss.util;

import com.cj.xss.exception.XssSqlException;
import org.springframework.util.StringUtils;

/**
 * SQL过滤
 *
 * @author 毕子航 951755883@qq.com
 * @date 2018/10/26
 */
public class SqlFilterKit {

	/**
	 * SQL注入过滤
	 *
	 * @param str 待验证的字符串
	 */
	public static String sqlInject(String str) {
		if (StringUtils.isEmpty(str)) {
			return null;
		}
		//去掉'|"|;|\字符
		str = StringUtils.replace(str, "'", "");
		str = StringUtils.replace(str, "\"", "");
		str = StringUtils.replace(str, ";", "");
		str = StringUtils.replace(str, "\\", "");

		//转换成小写
		str = str.toLowerCase();

		//非法字符
		String[] keywords = {"master", "truncate", "insert", "select", "delete", "update", "declare", "alter", "drop"};

		//判断是否包含非法字符
		for (String keyword : keywords) {
			if (str.indexOf(keyword) != -1) {
				throw new XssSqlException("包含非法字符");
			}
		}
		return str;
	}
}
