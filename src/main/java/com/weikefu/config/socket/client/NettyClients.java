package com.weikefu.config.socket.client;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.corundumstudio.socketio.SocketIOClient;

/**
 * nettyclient的工具类
 * 用于存放，广播，删除 nettyAgengClient，和UserClient对应的sockIoClient
 * 
 * @author Administrator 
 */
public class NettyClients {
	
	private static NettyClients clients = new NettyClients();
	
	private NettyOnlineCustClient customerClients = new NettyOnlineCustClient();
	private NettyUserClient userClients = new NettyUserClient();
	/**
	 * 获取nettyClient中的实例对象
	 * @return
	 */
	public static NettyClients getInstance(){
		return clients ;
	}
	
	/**
	 * 设置新的userClient
	 * @param imClients
	 */
	public void setUserClients(NettyUserClient imClients) {
		this.userClients = imClients;
	}
	/**
	 * 更具userid的id，放入新的socketIoClient
	 * @param id
	 * @param userClient
	 */
	public void putUserEventClient(String shopId,String userId , SocketIOClient userClient){
		userClients.putClient(shopId,userId, userClient);
	}
	/**
	 * 更具user的id和sessionid，删除SocketIoClient
	 * @param id
	 * @param sessionid
	 */
	public void removeUserEventClient(String shopId , String userId){
		userClients.removeClient(shopId,userId);
	}
	/**
	 * 根据id，发送消息
	 * @param id
	 * @param event
	 * @param data
	 */
	public void sendUserEventMessage(String shopId,String userId, String event , Object data){
		SocketIOClient userSocketClients = this.userClients.getClients(shopId,userId) ;
		if (userSocketClients!=null) {
			userSocketClients.sendEvent(event, data);
		}
	}
	
	/**
	 * 根据agent的id放入socketIOClient
	 * @param id
	 * @param agentClient
	 */
	public void putCustomerEventClient(String shopId ,String custId, SocketIOClient agentClient){
		this.customerClients.putClient(shopId, custId, agentClient);
	}
	
	/***
	 * 根据id和sessionid删除客服的socketIoClient。
	 * @param shopId
	 * @param custId
	 */
	public void removeCustomerEventClient(String shopId , String custId){
		this.customerClients.removeClient(shopId, custId);
	}
	/**
	 * 根据客服的id，和事件类型，发送对应的消息
	 * @param id
	 * @param event
	 * @param data
	 */
	public void sendCustomerEventMessage(String shopId , String custId,String event, Object data){
		SocketIOClient client = this.customerClients.getOnlineCustClient(shopId, custId);
		if (client != null) {
			client.sendEvent(event, data);
		}
	}
	
	/**
	 * 根据客服的id，和事件类型，多客服发送对应的消息
	 * @param id
	 * @param event
	 * @param data
	 */
	public void sendCustomerListEventMessage(String shopId, List<String> custIds, String userId, String event, Object data){
		for(int i=0;null!=custIds&&i<custIds.size();i++){
			SocketIOClient client = this.customerClients.getOnlineCustClient(shopId, custIds.get(i));
			String curUserId = NettyCustCurrentDialogUserMap.getCurrentUserId(shopId, custIds.get(i));
			if (client != null && userId.equals(curUserId)) {
				client.sendEvent(event, data);
			}
		}
	}
	
	/**
	 * 发现该用户是否真的存在其中
	 * @param ShopId
	 * @param custId
	 */
	public SocketIOClient findCustClient(String shopId,String custId){
		return this.customerClients.getOnlineCustClient(shopId, custId);
	
	}

}
