package com.cj.xss.filter;


import com.cj.xss.servlet.XssHttpServletRequestWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * XSS过滤器
 *
 * @author 毕子航 951755883@qq.com
 * @date 2018/10/26
 */
public class XssFilter implements Filter {

	public boolean enableSqlFilter = false;

	public void setEnableSqlFilter(boolean enableSqlFilter) {
		this.enableSqlFilter = enableSqlFilter;
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		XssHttpServletRequestWrapper xssRequest = new XssHttpServletRequestWrapper((HttpServletRequest) request);
		xssRequest.setAttribute(XssHttpServletRequestWrapper.ENABLE_SQL_FILTER, enableSqlFilter);
		chain.doFilter(xssRequest, response);
	}

	@Override
	public void destroy() {
	}
}