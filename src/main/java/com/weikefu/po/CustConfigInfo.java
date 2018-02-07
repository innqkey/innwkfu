package com.weikefu.po;

import java.io.Serializable;

/**
 * 客服的xin
 * @author Administrator
 *
 */

public class CustConfigInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7346410276227739189L;
	
	//客服的id
	private Integer custid;
	//商户的id
	private Integer shopid;
	//最大的接入量
	private Integer maxusers;
	//服务状态（在线online、离线leave、忙碌busy）
	private String custserverstatus;
	//接入方式；1.pc(电脑)、2.phone(-手机)
	private String custjoinway;
	//客服名称
	private String custname;
	//更新时间
	private String updatetime;
	private String onlinetime;
	public Integer getCustid() {
		return custid;
	}
	public void setCustid(Integer custid) {
		this.custid = custid;
	}
	public Integer getShopid() {
		return shopid;
	}
	public void setShopid(Integer shopid) {
		this.shopid = shopid;
	}
	public Integer getMaxusers() {
		return maxusers;
	}
	public void setMaxusers(Integer maxusers) {
		this.maxusers = maxusers;
	}
	public String getCustserverstatus() {
		return custserverstatus;
	}
	public void setCustserverstatus(String custserverstatus) {
		this.custserverstatus = custserverstatus;
	}
	public String getCustjoinway() {
		return custjoinway;
	}
	public void setCustjoinway(String custjoinway) {
		this.custjoinway = custjoinway;
	}
	public String getCustname() {
		return custname;
	}
	public void setCustname(String custname) {
		this.custname = custname;
	}
	public String getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(String updatetime) {
		this.updatetime = updatetime;
	}
	public String getOnlinetime() {
		return onlinetime;
	}
	public void setOnlinetime(String onlinetime) {
		this.onlinetime = onlinetime;
	}
	@Override
	public String toString() {
		return "CustCinfigInfo [custid=" + custid + ", shopid=" + shopid
				+ ", maxusers=" + maxusers + ", custserverstatus="
				+ custserverstatus + ", custjoinway=" + custjoinway
				+ ", custname=" + custname + ", updatetime=" + updatetime
				+ ", onlinetime=" + onlinetime + "]";
	}
	
	
}
