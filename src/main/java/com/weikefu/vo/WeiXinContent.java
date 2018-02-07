package com.weikefu.vo;
/**
* 类说明：只是为了微信发送消息的一个封装，
* 应为用hashmap消耗的性能有点大
* @author 
* @version 创建时间：2018年1月29日 上午10:21:28
* 
*/
public class WeiXinContent {
	public String content;
	private String media_id;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	

	public String getMedia_id() {
		return media_id;
	}

	public void setMedia_id(String media_id) {
		this.media_id = media_id;
	}

	@Override
	public String toString() {
		return "WeiXinContent [content=" + content + ", media_id=" + media_id + "]";
	}

	
	
	
}
