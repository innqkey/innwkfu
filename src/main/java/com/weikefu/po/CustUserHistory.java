package com.weikefu.po;
/**
* 类说明：
* @author 
* @version 创建时间：2018年2月2日 下午3:43:01
* 
*/

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.weikefu.annotation.AutoIncKey;

@Document(collection = "kefu_cust_user_history")
public class CustUserHistory {
	
	@Id
	@AutoIncKey
	private Long id = 0L;
	@Indexed
	private String shopid;
	@Indexed
	private String custid;
	
	private String userid;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getShopid() {
		return shopid;
	}

	public void setShopid(String shopid) {
		this.shopid = shopid;
	}

	public String getCustid() {
		return custid;
	}

	public void setCustid(String custid) {
		this.custid = custid;
	}


	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}


	
}
