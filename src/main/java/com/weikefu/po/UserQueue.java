package com.weikefu.po;

import java.io.Serializable;

/**
 * 用户排序的队列
 * @author Administrator
 *
 */
public class UserQueue implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8135567855184571314L;
	
	//会话id
	private Integer dialogid;
	// 商户的id
	private Integer shopid;
	//客服id
	private Integer custid;
	//用户id
	private Integer userid;
	//用户名称
	private String username;
	//客服名称
	private String custname;
	private String createtime;
	//接入时候
	private String intime;
	//结束时间
	private String outtime;
	//接入方式 1. 微信  2.手机
	private String joinway;
	public Integer getDialogid() {
		return dialogid;
	}
	public void setDialogid(Integer dialogid) {
		this.dialogid = dialogid;
	}
	public Integer getShopid() {
		return shopid;
	}
	public void setShopid(Integer shopid) {
		this.shopid = shopid;
	}
	public Integer getCustid() {
		return custid;
	}
	public void setCustid(Integer custid) {
		this.custid = custid;
	}
	public Integer getUserid() {
		return userid;
	}
	public void setUserid(Integer userid) {
		this.userid = userid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getCustname() {
		return custname;
	}
	public void setCustname(String custname) {
		this.custname = custname;
	}
	public String getCreatetime() {
		return createtime;
	}
	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}
	public String getIntime() {
		return intime;
	}
	public void setIntime(String intime) {
		this.intime = intime;
	}
	public String getOuttime() {
		return outtime;
	}
	public void setOuttime(String outtime) {
		this.outtime = outtime;
	}
	public String getJoinway() {
		return joinway;
	}
	public void setJoinway(String joinway) {
		this.joinway = joinway;
	}
	@Override
	public String toString() {
		return "UserQueue [dialogid=" + dialogid + ", shopid=" + shopid
				+ ", custid=" + custid + ", userid=" + userid + ", username="
				+ username + ", custname=" + custname + ", createtime="
				+ createtime + ", intime=" + intime + ", outtime=" + outtime
				+ ", joinway=" + joinway + "]";
	}
	
	
	
}
