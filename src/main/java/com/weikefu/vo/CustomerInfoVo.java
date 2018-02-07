package com.weikefu.vo;

import java.util.List;

/**
 * 用于存放初始化Customer时候的
 * 参数
 * @author Administrator
 *
 */
public class CustomerInfoVo {
    private Integer id;
    /**
     * 客服id
     */
    private Integer custid;

    /**
     * 客服名称
     */
    private String custname;

    /**
     * 商户id
     */
    private Integer shopid;

    /**
     * 服务状态（在线online、离线leave、忙碌busy）
     */
    private String custserverstatus;

    /**
     * 最大接入数量（5、10、15）
     */
    private Integer maxusernum;
    private String headurl;
    /**
     * 接入用户数量
     */
    private Integer joinusernum;
    
    private List<UserInfoVo> userInfoVos;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCustid() {
		return custid;
	}

	public void setCustid(Integer custid) {
		this.custid = custid;
	}

	public String getCustname() {
		return custname;
	}

	public void setCustname(String custname) {
		this.custname = custname;
	}

	public Integer getShopid() {
		return shopid;
	}

	public void setShopid(Integer shopid) {
		this.shopid = shopid;
	}

	public String getCustserverstatus() {
		return custserverstatus;
	}

	public void setCustserverstatus(String custserverstatus) {
		this.custserverstatus = custserverstatus;
	}

	public Integer getMaxusernum() {
		return maxusernum;
	}

	public void setMaxusernum(Integer maxusernum) {
		this.maxusernum = maxusernum;
	}

	public String getHeadurl() {
		return headurl;
	}

	public void setHeadurl(String headurl) {
		this.headurl = headurl;
	}

	public Integer getJoinusernum() {
		return joinusernum;
	}

	public void setJoinusernum(Integer joinusernum) {
		this.joinusernum = joinusernum;
	}

	public List<UserInfoVo> getUserInfoVos() {
		return userInfoVos;
	}

	public void setUserInfoVos(List<UserInfoVo> userInfoVos) {
		this.userInfoVos = userInfoVos;
	}
    
    
}
