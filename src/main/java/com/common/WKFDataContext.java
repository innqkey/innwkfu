package com.common;

import org.springframework.context.ApplicationContext;

public class WKFDataContext {
	
	//设置服务器对应的状态
	private static boolean serverStatus = false;
	//spring的上下文用于获取spring 对应的对象
	private static ApplicationContext applicationContext;
	
	//用于加密的
	public static String getSystemSecrityPassword() {
		return "WeKeFuHuiSou";
	}
	/**
	 * 设置服务器对应的状态
	 * @param b
	 */
	public static void setImServerStatus(boolean b) {
		serverStatus = b;
	}
	
	
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}
	public static void setApplicationContext(ApplicationContext app) {
		applicationContext = app;
	}

	
}
