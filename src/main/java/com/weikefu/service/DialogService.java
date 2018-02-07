package com.weikefu.service;

import java.util.List;

import com.weikefu.vo.UserInfoVo;

public interface DialogService {

	/*
	 * 获取当前会话对象，用于会话页展示，用于action
	 */
	public String dialogList(String shopId, String custId);
	
	/*
	 * 页面排队队列监听事件，发送socketio
	 * custClient.sendEvent("waitQueue", joinUserList);
	 */
	public void waitDialogList(String shopId);
	
	/*
	 * 获取排队对象，用于会话也展示，用于action调用
	 */
	public List<UserInfoVo> queueUserInfoVoList(String shopId, String custId);
}
