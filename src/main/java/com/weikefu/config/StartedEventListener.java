package com.weikefu.config;


import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.common.WKFDataContext;

/**
 * 项目启动的监听器,可以用来加载系统文件等
 * @author Administrator
 *
 */
@Component
public class StartedEventListener implements ApplicationListener<ContextRefreshedEvent> {

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (WKFDataContext.getApplicationContext() == null) {
			WKFDataContext.setApplicationContext(event.getApplicationContext());
		}
	}


}
