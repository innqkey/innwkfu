package com.weikefu.vo.smallroutine;

import org.hibernate.validator.constraints.NotEmpty;

/**
* 类说明：
* @author 
* @version 创建时间：2018年1月31日 上午10:29:17
* 
*/


public class Message {
	
	private String FromUserName;
	private String ToUserName;
	private String MsgType;
	//文档的内容
	private String Content;
	//图片的内容
	private String PicUrl;
	private String MediaId;
	//这个是小程序的卡片
	private String Title;
	private String AppId;
	//小程序的页面的路径
	private String PagePath;
	//临时素材的cdn
	private String ThumbUrl;
	//临时封面图片的临时素材的id
	private String ThumbMediaId;
	public String getFromUserName() {
		return FromUserName;
	}
	public void setFromUserName(String fromUserName) {
		FromUserName = fromUserName;
	}
	public String getToUserName() {
		return ToUserName;
	}
	public void setToUserName(String toUserName) {
		ToUserName = toUserName;
	}
	public String getMsgType() {
		return MsgType;
	}
	public void setMsgType(String msgType) {
		MsgType = msgType;
	}
	public String getContent() {
		return Content;
	}
	public void setContent(String content) {
		Content = content;
	}
	public String getPicUrl() {
		return PicUrl;
	}
	public void setPicUrl(String picUrl) {
		PicUrl = picUrl;
	}
	public String getMediaId() {
		return MediaId;
	}
	public void setMediaId(String mediaId) {
		MediaId = mediaId;
	}
	public String getTitle() {
		return Title;
	}
	public void setTitle(String title) {
		Title = title;
	}
	public String getAppId() {
		return AppId;
	}
	public void setAppId(String appId) {
		AppId = appId;
	}
	public String getPagePath() {
		return PagePath;
	}
	public void setPagePath(String pagePath) {
		PagePath = pagePath;
	}
	public String getThumbUrl() {
		return ThumbUrl;
	}
	public void setThumbUrl(String thumbUrl) {
		ThumbUrl = thumbUrl;
	}
	public String getThumbMediaId() {
		return ThumbMediaId;
	}
	public void setThumbMediaId(String thumbMediaId) {
		ThumbMediaId = thumbMediaId;
	}
	@Override
	public String toString() {
		return "Message [FromUserName=" + FromUserName + ", ToUserName=" + ToUserName + ", MsgType=" + MsgType
				+ ", Content=" + Content + ", PicUrl=" + PicUrl + ", MediaId=" + MediaId + ", Title=" + Title
				+ ", AppId=" + AppId + ", PagePath=" + PagePath + ", ThumbUrl=" + ThumbUrl + ", ThumbMediaId="
				+ ThumbMediaId + "]";
	}
	
}
