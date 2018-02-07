package com.weikefu.vo.smallroutine;

import java.util.Date;

/**
* 类说明：
* @author 
* @version 创建时间：2018年1月25日 上午11:39:26
* 
*/
public class DialogEvent {
	
	//商店的id
	private String shopId;
	//小程序原始的id
	private String  ToUserName;
	//发送者的openid
	private String  FromUserName;
	//事件创建时间(整型）
	private Date CreateTime;
	private String MsgType;
	//	事件类型，user_enter_tempsession
	private String Event;
	//开发者在客服会话按钮设置的session-from属性
	private String SessionFrom;
	
	
	public String getShopId() {
		return shopId;
	}
	public void setShopId(String shopId) {
		this.shopId = shopId;
	}
	public String getToUserName() {
		return ToUserName;
	}
	public void setToUserName(String toUserName) {
		ToUserName = toUserName;
	}
	public String getFromUserName() {
		return FromUserName;
	}
	public void setFromUserName(String fromUserName) {
		FromUserName = fromUserName;
	}
	public Date getCreateTime() {
		return CreateTime;
	}
	public void setCreateTime(Date createTime) {
		CreateTime = createTime;
	}
	public String getMsgType() {
		return MsgType;
	}
	public void setMsgType(String msgType) {
		MsgType = msgType;
	}
	public String getEvent() {
		return Event;
	}
	public void setEvent(String event) {
		Event = event;
	}
	public String getSessionFrom() {
		return SessionFrom;
	}
	public void setSessionFrom(String sessionFrom) {
		SessionFrom = sessionFrom;
	}
	@Override
	public String toString() {
		return "DialogEvent [ToUserName=" + ToUserName + ", FromUserName=" + FromUserName + ", CreateTime=" + CreateTime
				+ ", MsgType=" + MsgType + ", Event=" + Event + ", SessionFrom=" + SessionFrom + "]";
	}

}
