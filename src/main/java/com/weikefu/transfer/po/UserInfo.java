package com.weikefu.transfer.po;

import java.io.Serializable;
/**
 * 保存用户的对应的信息
 * @author Administrator
 *
 */
public class UserInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2881222089268535495L;
	

	//微商城用户id
	private String userId;

	//使用微信的id
	
	private String openid;
	//头像的名称
	private String headimgurl;
	//昵称

	private String nickname;
	
	private String truename;
	
	private String mobile;


	//unionid  绑定的公众号的id ，结合可以发送消息相关的消息
	private String unionId;
	//接入的方式
	private String joinway;

	private String createdat;
	
	public String getCreatedat() {
		return createdat;
	}

	public void setCreatedat(String createdat) {
		this.createdat = createdat;
	}

	public String getUnionId() {
		return unionId;
	}

	public void setUnionId(String unionId) {
		this.unionId = unionId;
	}

	public String getJoinway() {
		return joinway;
	}

	public void setJoinway(String joinway) {
		this.joinway = joinway;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	private String province;
	
	private String city;
	private String country;
	
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
	public String getTruename() {
		return truename;
	}
	public void setTruename(String truename) {
		this.truename = truename;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}

	@Override
	public String toString() {
		return "UserInfo [userId=" + userId + ", openid=" + openid
				+ ", headimgurl=" + headimgurl + ", nickname=" + nickname
				+ ", truename=" + truename + ", mobile=" + mobile
				+ ", unionId=" + unionId + ", joinway=" + joinway
				+ ", createdat=" + createdat + ", province=" + province
				+ ", city=" + city + ", country=" + country + "]";
	}
	
	
	
}
