package com.weikefu.vo;

public class CustomerBaseInfo {

	private String custId;
	//昵称
	private String custname;
	//头像的名称5
	private String headurl;
	
	private String shopId;
	
	private Integer isheader;
	
	private String crm_token;
	
	//店铺名称
	private String shopname;
	//店铺图标
	private String logo;
	
	public String getShopname() {
		return shopname;
	}
	public void setShopname(String shopname) {
		this.shopname = shopname;
	}
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public String getShopId() {
		return shopId;
	}
	public void setShopId(String shopId) {
		this.shopId = shopId;
	}
	public String getCustname() {
		return custname;
	}
	public void setCustname(String custname) {
		this.custname = custname;
	}
	public String getHeadurl() {
		return headurl;
	}
	public void setHeadurl(String headurl) {
		this.headurl = headurl;
	}
	public Integer getIsheader() {
		return isheader;
	}
	public void setIsheader(Integer isheader) {
		this.isheader = isheader;
	}
	
	public String getCrm_token() {
		return crm_token;
	}
	public void setCrm_token(String crm_token) {
		this.crm_token = crm_token;
	}
	public String getCustId() {
		return custId;
	}
	public void setCustId(String custId) {
		this.custId = custId;
	}

}
