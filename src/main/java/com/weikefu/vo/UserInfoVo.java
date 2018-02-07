package com.weikefu.vo;

import java.util.Calendar;
import java.util.Date;

import org.springframework.data.annotation.Transient;

import com.weikefu.constant.ContextConstant;
import com.weikefu.util.DateUtils;

/**
 * 用于初始化用户信息的Vo
 * @author Administrator
 *
 */
public class UserInfoVo {
	
	private String userId;
	//使用微信的id
	private String openid;
	//头像的名称
	private String headimgurl;
	//昵称

	private String nickname;


	private String shopId;


	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getHeadimgurl() {
		return headimgurl;
	}
	public void setHeadimgurl(String headimgurl) {
		this.headimgurl = headimgurl;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getMsgtype() {
		return msgtype;
	}
	public void setMsgtype(String msgtype) {
		this.msgtype = msgtype;
	}
	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}
	public Boolean getCloseShow() {
		return closeShow;
	}
	public void setCloseShow(Boolean closeShow) {
		this.closeShow = closeShow;
	}
	public long getMsgcount() {
		return msgcount;
	}
	public void setMsgcount(long msgcount) {
		this.msgcount = msgcount;
	}
	//用于保存用户的最后一条消息
	private String message;
	private String msgtype;
	private Boolean active=false;
	private Boolean closeShow = false;
	private String time;
	
	//用来进行图片的fangd
	private Boolean innerVisible=false;

	//msg的数量
	private long msgcount;

	private String sendWay;

	//窗口的消息类型 分别为chat 和Order
	private String type = ContextConstant.WINDOW_CHAT;
	@Transient
	private Date timeTemp;
	
	private String joinway;
	
	
	public String getJoinway() {
		return joinway;
	}
	
	public void setJoinway(String joinway) {
		this.joinway = joinway;
	}
	public String getShopId() {
		return shopId;
	}
	public void setShopId(String shopId) {
		this.shopId = shopId;

	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Date getTimeTemp() {
		if(null==timeTemp){
			return new Date();
		}
		return timeTemp;
	}
	public void setTimeTemp(Date timeTemp) {
		this.timeTemp = timeTemp;
	}
	/**
	 * 对时间进行处理，是今天就显示时分秒，不是显示日期
	 * @return
	 */
	public String getTime() {
		if (this.timeTemp != null) {
			Calendar instance = Calendar.getInstance();
			Date date = new Date();
			String format = DateUtils.format(date, DateUtils.ymd);
			String format1 = DateUtils.format(this.timeTemp, DateUtils.ymd);
			if (format.equals(format1)) {
				Calendar cal=Calendar.getInstance(); 
				cal.setTime(this.timeTemp);
				String hour = addZero(cal.get(Calendar.HOUR_OF_DAY));
				String minute = addZero(cal.get(Calendar.MINUTE));
				
				return new StringBuilder().append(hour).append(":").append(minute).toString();
			}else {
				StringBuilder builder = new StringBuilder();
				builder.append(DateUtils.getMonth(this.timeTemp)).append("/").append(DateUtils.getDay(this.timeTemp));
				return builder.toString() ;
				
			}
		}
		
		
		return time;
	}
	private String addZero(int i) {
		if (i < 10) {
			return "0" + i;
		}
		return i + "";
	}
	public void setTime(String time) {
		this.time = time;
	}
	

}
