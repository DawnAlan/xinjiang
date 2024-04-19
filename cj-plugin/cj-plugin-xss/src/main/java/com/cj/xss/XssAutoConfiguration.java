package com.cj.xss;

import com.cj.xss.filter.XssFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.DispatcherType;

/**
 * @author 毕子航 951755883@qq.com
 * @date 2018/10/26
 */
@Configuration
@EnableConfigurationProperties({XssProperties.class})
public class XssAutoConfiguration {

	@Autowired
	XssProperties xssProperties;

	@Bean
	@ConditionalOnProperty(prefix = XssProperties.XSS, name = "enable", havingValue = "true", matchIfMissing = false)
	public FilterRegistrationBean xssFilterRegistration() {
		XssFilter xssFilter = new XssFilter();
		xssFilter.setEnableSqlFilter(xssProperties.isSqlFilter());
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setDispatcherTypes(DispatcherType.REQUEST);
		registration.setFilter(xssFilter);
		registration.addUrlPatterns(xssProperties.getUrlPatterns());
		registration.setName(xssProperties.getName());
		registration.setOrder(xssProperties.getOrder());
		return registration;
	}
}
