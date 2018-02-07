package com.weikefu.config.intercetpor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WKFWebAppConfiger extends WebMvcConfigurerAdapter {
	@Autowired
	private LoginIntercetpor loginIntercetpor;
	
	/**
	 * 注册拦截器
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(loginIntercetpor)
					.addPathPatterns("/**")
					.excludePathPatterns("/authority/**")
					.excludePathPatterns("/file/**")
					.excludePathPatterns("/error");
		super.addInterceptors(registry);
	}
}
