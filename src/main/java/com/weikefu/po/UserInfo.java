package com.weikefu.po;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * 保存用户的对应的信息
 * @author Administrator
 *
 */
@Document(collection= "kefu_user")
public class UserInfo implements Serializable{
	private static final long serialVersionUID = 2881222089268535495L;
	//使用自己生成的用户的id
	@Id
	private String  userId;
	//用来保存微商城的用户的id
	private String weiuserid;
	//使用微信的id
	@Field("openid")//字段还可以用自定义名称
	@Indexed
    private String openid;
	@Field
	private String access_token;
	@Field
	private Long expireTime;
	
	//头像的名称
	@Field
	private String headimgurl;
	//昵称
	@Field
	private String nickname;
	@Field
	private String truename;
	@Field
	private String mobile;
	//unionid  绑定的公众号的id ，结合可以发送消息相关的消息
	@Field
	private String unionId;
	//接入的方式
	@Field
	@Indexed
	private String joinway;
	@Field
	private String province;
	@Field
	private String city;
	@Field
	private String country;
	@Field
	private String created_at;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getWeiuserid() {
		return weiuserid;
	}
	public void setWeiuserid(String weiuserid) {
		this.weiuserid = weiuserid;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getAccess_token() {
		return access_token;
	}
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
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

	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	
	public Long getExpireTime() {
		return expireTime;
	}
	public void setExpireTime(Long expireTime) {
		this.expireTime = expireTime;
	}
	@Override
	public String toString() {
		return "UserInfo [userId=" + userId + ", weiuserid=" + weiuserid + ", openid=" + openid + ", access_token="
				+ access_token + ", headimgurl=" + headimgurl + ", nickname=" + nickname + ", truename=" + truename
				+ ", mobile=" + mobile + ", unionId=" + unionId + ", joinway=" + joinway + ", province=" + province
				+ ", city=" + city + ", country=" + country + ", createdat=" + created_at + "]";
	}


}
