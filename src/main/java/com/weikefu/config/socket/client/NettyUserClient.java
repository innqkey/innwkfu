package com.weikefu.config.socket.client;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.corundumstudio.socketio.SocketIOClient;
import com.google.common.collect.ArrayListMultimap;

/**
 * nettyImClient用来存放user端的
 * @author Administrator
 *
 */
public class NettyUserClient implements NettyClient{
	private final static Logger userLogger = LoggerFactory.getLogger(NettyUserClient.class);
	private ConcurrentHashMap<String, SocketIOClient> imclient = new ConcurrentHashMap<>();
	@Override
	public SocketIOClient getClients(String shopId,String userId){
		return imclient.get(shopId + "-" + userId) ;
	}
	@Override
	public void putClient(String shopId,String userId, SocketIOClient client){
		if(null!=client){
			imclient.put(shopId + "-" + userId, client) ;
			userLogger.info("在线user用户socketio数量==="+imclient.size());
		}
	}
	@Override
	public void removeClient(String shopId,String userId){
		imclient.remove(shopId + "-" + userId);
	}


}
