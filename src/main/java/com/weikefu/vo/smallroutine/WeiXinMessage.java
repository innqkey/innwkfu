package com.weikefu.vo.smallroutine;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import org.hibernate.validator.constraints.NotEmpty;

/**
* 类说明：
* @author 
* @version 创建时间：2018年1月31日 上午10:28:16
* 
*/
public class WeiXinMessage {
	@NotEmpty(message="shopId不能为空")
	private String shopId;
	@NotNull
	private Map<String, String> message;

	public String getShopId() {
		return shopId;
	}

	public void setShopId(String shopId) {
		this.shopId = shopId;
	}

	


	public Map<String, String> getMessage() {
		return message;
	}

	public void setMessage(Map<String, String> message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "WeiXinMessage [shopId=" + shopId + ", message=" + message + "]";
	}



	

	
	
}
