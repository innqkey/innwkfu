package com.weikefu.config.socket.client;

import java.util.List;

import com.corundumstudio.socketio.SocketIOClient;
import com.google.common.collect.ArrayListMultimap;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;  

/**
 * netty在线客服的sockioClient的存储对象
 * 客服onconnect后创建io，shopid-custid为主键，存储在线客服io，用户接入后放入用户的会话房间
 * @author Administrator
 *
 */
public class NettyOnlineCustClient{
	private final static Logger custLogger = LoggerFactory.getLogger(NettyOnlineCustClient.class);
	//放入对象，在线客服客服SocketIOClient，key=shopId-custId
	private static ConcurrentMap<String, SocketIOClient> onlineCustClientsMap = new ConcurrentHashMap<String, SocketIOClient>();
	
	public static SocketIOClient getOnlineCustClient(String shopId, String custId){
		return onlineCustClientsMap.get(shopId+"-"+custId);
	}
	
	public static void putClient(String shopId, String custId, SocketIOClient client){
		if(null!=client){
			onlineCustClientsMap.put(shopId+"-"+custId, client);
			custLogger.info("在线cust客服socketio数量==="+onlineCustClientsMap.size());
		}
	}
	
	public static void removeClient(String shopId, String custId){
		onlineCustClientsMap.remove(shopId+"-"+custId);
	}
}
