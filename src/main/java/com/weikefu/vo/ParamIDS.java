package com.weikefu.vo;

public class ParamIDS {
	private String shopId;
	private String custId;
	private String userId;
	private String joinWay;
	private String sign;
	private String custStatus;
	
	public String getCustStatus() {
		return custStatus;
	}
	public void setCustStatus(String custStatus) {
		this.custStatus = custStatus;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public String getShopId() {
		return shopId;
	}
	public void setShopId(String shopId) {
		this.shopId = shopId;
	}
	public String getCustId() {
		return custId;
	}
	public void setCustId(String custId) {
		this.custId = custId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getJoinWay() {
		return joinWay;
	}
	public void setJoinWay(String joinWay) {
		this.joinWay = joinWay;
	}
	@Override
	public String toString() {
		return "ParamIDS [shopId=" + shopId + ", custId=" + custId + ", userId=" + userId + ", joinWay=" + joinWay
				+ ", sign=" + sign + ", custStatus=" + custStatus + "]";
	}
	
	
}
