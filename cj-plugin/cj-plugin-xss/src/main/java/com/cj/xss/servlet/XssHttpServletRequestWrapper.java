package com.cj.xss.servlet;


import com.cj.xss.util.HtmlFilterKit;
import com.cj.xss.util.SqlFilterKit;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * XSS过滤处理
 *
 * @author 毕子航 951755883@qq.com
 * @date 2018/10/26
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {
	public static final String ENABLE_SQL_FILTER = "_enable_sql_filter";
	public static final String APPLICATION_JSON_VALUE = "application/json";
	public static final String CONTENT_TYPE = "Content-Type";

	/**
	 * 没被包装过的HttpServletRequest（特殊场景，需要自己过滤）
	 */
	HttpServletRequest orgRequest;
	/**
	 * html过滤
	 */
	private final static HtmlFilterKit HTML_FILTER = new HtmlFilterKit();

	private boolean enableSqlFilter = false;

	public XssHttpServletRequestWrapper(HttpServletRequest request) {
		super(request);
		orgRequest = request;
		enableSqlFilter = (boolean) request.getAttribute(ENABLE_SQL_FILTER);
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		//非json类型，直接返回
		if (!APPLICATION_JSON_VALUE.equalsIgnoreCase(super.getHeader(CONTENT_TYPE))) {
			return super.getInputStream();
		}

		//为空，直接返回
		String json = StreamUtils.copyToString(super.getInputStream(), Charset.forName("UTF-8"));
		if (StringUtils.isEmpty(json)) {
			return super.getInputStream();
		}

		//xss过滤
		json = xssEncode(json);
		final ByteArrayInputStream bis = new ByteArrayInputStream(json.getBytes("UTF-8"));
		return new ServletInputStream() {
			@Override
			public boolean isFinished() {
				return true;
			}

			@Override
			public boolean isReady() {
				return true;
			}

			@Override
			public void setReadListener(ReadListener readListener) {
			}

			@Override
			public int read() throws IOException {
				return bis.read();
			}
		};
	}

	@Override
	public String getParameter(String name) {
		String value = super.getParameter(xssEncode(name));
		if (!StringUtils.isEmpty(value)) {
			value = xssEncode(value);
		}
		return value;
	}

	@Override
	public String[] getParameterValues(String name) {
		String[] parameters = super.getParameterValues(name);
		if (parameters == null || parameters.length == 0) {
			return null;
		}

		for (int i = 0; i < parameters.length; i++) {
			parameters[i] = xssEncode(parameters[i]);
		}
		return parameters;
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		Map<String, String[]> map = new LinkedHashMap<>();
		Map<String, String[]> parameters = super.getParameterMap();
		for (String key : parameters.keySet()) {
			String[] values = parameters.get(key);
			for (int i = 0; i < values.length; i++) {
				values[i] = xssEncode(values[i]);
			}
			map.put(key, values);
		}
		return map;
	}

	@Override
	public String getHeader(String name) {
		String value = super.getHeader(xssEncode(name));
		if (!StringUtils.isEmpty(value)) {
			value = xssEncode(value);
		}
		return value;
	}

	private String xssEncode(String input) {


		if (enableSqlFilter) {
			return HTML_FILTER.filter(SqlFilterKit.sqlInject(input));
		}
		return HTML_FILTER.filter(input);
	}

	/**
	 * 获取最原始的request
	 */
	public HttpServletRequest getOrgRequest() {
		return orgRequest;
	}

	/**
	 * 获取最原始的request
	 */
	public static HttpServletRequest getOrgRequest(HttpServletRequest request) {
		if (request instanceof XssHttpServletRequestWrapper) {
			return ((XssHttpServletRequestWrapper) request).getOrgRequest();
		}
		return request;
	}
}
