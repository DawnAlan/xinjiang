package com.cj.xss.exception;

/**
 * Xss sql过滤
 *
 * @author 毕子航 951755883@qq.com
 * @date 2018/10/26
 */
public class XssSqlException extends RuntimeException {

	public XssSqlException() {
	}

	public XssSqlException(String message) {
		super(message);
	}

	public XssSqlException(String message, Throwable cause) {
		super(message, cause);
	}

	public XssSqlException(Throwable cause) {
		super(cause);
	}

	public XssSqlException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
