package com.weikefu.config.socket;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.common.WKFDataContext;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.weikefu.config.socket.handler.CustomerEventHandler;
import com.weikefu.config.socket.handler.UserEventHandler;
import com.weikefu.constant.ContextConstant;


/**
 * 用于项目启动的时候，启动socket服务器
 * 当服务器
 * @author Administrator
 *
 */
@Component
public class ServerRunner implements CommandLineRunner {
	
	private Logger logger = LoggerFactory.getLogger(ServerRunner.class);
	private final SocketIONamespace imSocketNameSpace;
	private final SocketIONamespace agentSocketIONameSpace ;
	private SocketIOServer server;
	@Autowired
	private UserEventHandler userEventHandler;
	
	@Autowired
	private CustomerEventHandler customerEventHandler;
	/**
	 * 用于注入server
	 */
	@Autowired
	public ServerRunner(SocketIOServer server) {
		this.server = server;
		imSocketNameSpace = server.addNamespace(ContextConstant.NameSpaceEnum.IM.getNamespace());
		agentSocketIONameSpace = server.addNamespace(ContextConstant.NameSpaceEnum.AGENT.getNamespace());
	}
	/**
	 * 为什么不直接通过注入而是将对应的对象交给spring去管理？
	 *  可以便于以后使用对应的名称空间
	 * @return
	 */
	@Bean("imNamespace")   
    public SocketIONamespace getIMSocketIONameSpace(){
    	imSocketNameSpace.addListeners(userEventHandler);
    	return imSocketNameSpace;
    }
    
    @Bean(name="agentNamespace")
    public SocketIONamespace getAgentSocketIONameSpace(){
    	agentSocketIONameSpace.addListeners(customerEventHandler);
    	return agentSocketIONameSpace;
    }

	/**
	 * 启动socket
	 */
	@Override
	public void run(String... args) throws Exception {
		logger.info("socket server  satarting");
		server.start();
		//当项目启动的时候需要改变状态。
		WKFDataContext.setImServerStatus(true);
	}

}
