package com.weikefu.service;

import java.util.List;

import com.corundumstudio.socketio.SocketIOClient;
import com.weikefu.vo.CustomerBaseInfo;

public interface AllotCustService {

	public void userJoin(String userId, String shopId, SocketIOClient client);
	
	public void custJoin(String custId, String shopId, SocketIOClient client, String kefuStatus);
	
	//客服遍历排队队列，与会话队列比较，如果数据重复，删除排队队列，并发送排队用户变更事件
	public void compareCustDialogAndQueue(String custId, String shopId);
	
	/*
	 * 根据userId+shopId获取聊天室，遍历聊天室获取在线客服custSocket，发送当前会话用户接入事件
	 */
	public void currentDialogJoin(String shopId, String userId);
	
	public void custListJoinTalk(String shopId, String userId, List<String> custIds);
	
	//发送聊天室客服变化通知时间
	public void sendTalkCustEvent(String shopId, String userId);
	
	//获取聊天室客服列表信息
	public List<CustomerBaseInfo> talkCustBaseInfoList(String shopId, String userId);

	public void sendCurrentDialogEvent(String custId, String shopId, SocketIOClient custClient);
}
