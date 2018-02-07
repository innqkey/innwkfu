package com.weikefu.vo.smallroutine;

import java.util.Map;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

/**
* 类说明：
* @author 
* @version 创建时间：2018年1月31日 上午11:55:51
* 
*/
public class WeiXinUserInfo {
	private String token;
	private Long expireTime;
	
	@NotEmpty(message="shopId不能为空")
	private String shopId;
	@NotNull
	private Map<String, String> user;
	public String getShopId() {
		return shopId;
	}
	public void setShopId(String shopId) {
		this.shopId = shopId;
	}
	public Map<String, String> getUser() {
		return user;
	}
	public void setUser(Map<String, String> user) {
		this.user = user;
	}
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public Long getExpireTime() {
		return expireTime;
	}
	public void setExpireTime(Long expireTime) {
		this.expireTime = expireTime;
	}
	@Override
	public String toString() {
		return "WeiXinUserInfo [token=" + token + ", expireTime=" + expireTime + ", shopId=" + shopId + ", user=" + user
				+ "]";
	}
	

}
