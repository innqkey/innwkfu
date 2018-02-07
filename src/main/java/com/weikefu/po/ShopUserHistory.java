package com.weikefu.po;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.weikefu.annotation.AutoIncKey;

/**
* 类说明：
* @author 
* @version 创建时间：2018年2月1日 下午7:49:40
* 
*/
@Document(collection = "kefu_shop_user_history")
public class ShopUserHistory implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3789968509178364882L;
	@Id
	@AutoIncKey
	private Long id ;
	@Indexed
	private String shopid;
	@Indexed
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

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	@Override
	public String toString() {
		return "ShopUserHistory [id=" + id + ", shopid=" + shopid + ", userid=" + userid + "]";
	}

	
}
