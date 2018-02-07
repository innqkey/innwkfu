package com.weikefu.po;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import org.springframework.data.mongodb.core.mapping.Document;

import com.weikefu.annotation.AutoIncKey;

//@Table(name = "kefu_shop_cust_status")
@Document(collection = "kefu_shop_cust_status")
public class ShopCustPo implements Serializable {
    /**
     * 主键
     */
//	@Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@org.springframework.data.annotation.Id
    private String id ;

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

    /**
     * 接入用户数量
     */
    private Integer joinusernum;

    /**
     * 创建时间
     */
    private Date createtime;

    /**
     * 更新时间
     */
    private Date updatetime;

    /**
     * 在线时长
     */
    private Long onlinetimelong;
    
    /**
     * 服务的serveruserID
     */
    private String serveruserid;

    /**
     * 备用字段1
     */
    private String headurl;

    /**
     * 备用字段2
     */
    private String standby2;
    
    private String isheader;
    

    private static final long serialVersionUID = 1L;

   
    
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
     * 获取客服id
     *
     * @return custid - 客服id
     */
    public Integer getCustid() {
        return custid;
    }

    /**
     * 设置客服id
     *
     * @param custid 客服id
     */
    public void setCustid(Integer custid) {
        this.custid = custid;
    }

    /**
     * 获取客服名称
     *
     * @return custname - 客服名称
     */
    public String getCustname() {
        return custname;
    }

    /**
     * 设置客服名称
     *
     * @param custname 客服名称
     */
    public void setCustname(String custname) {
        this.custname = custname;
    }

    /**
     * 获取商户id
     *
     * @return shopid - 商户id
     */
    public Integer getShopid() {
        return shopid;
    }

    /**
     * 设置商户id
     *
     * @param shopid 商户id
     */
    public void setShopid(Integer shopid) {
        this.shopid = shopid;
    }

    /**
     * 获取服务状态（在线online、离线leave、忙碌busy）
     *
     * @return custserverstatus - 服务状态（在线online、离线leave、忙碌busy）
     */
    public String getCustserverstatus() {
        return custserverstatus;
    }

    /**
     * 设置服务状态（在线online、离线leave、忙碌busy）
     *
     * @param custserverstatus 服务状态（在线online、离线leave、忙碌busy）
     */
    public void setCustserverstatus(String custserverstatus) {
        this.custserverstatus = custserverstatus;
    }

    /**
     * 获取最大接入数量（5、10、15）
     *
     * @return maxusernum - 最大接入数量（5、10、15）
     */
    public Integer getMaxusernum() {
        return maxusernum;
    }

    /**
     * 设置最大接入数量（5、10、15）
     *
     * @param maxusernum 最大接入数量（5、10、15）
     */
    public void setMaxusernum(Integer maxusernum) {
        this.maxusernum = maxusernum;
    }

    /**
     * 获取接入用户数量
     *
     * @return joinusernum - 接入用户数量
     */
    public Integer getJoinusernum() {
        return joinusernum;
    }

    /**
     * 设置接入用户数量
     *
     * @param joinusernum 接入用户数量
     */
    public void setJoinusernum(Integer joinusernum) {
        this.joinusernum = joinusernum;
    }

    /**
     * 获取创建时间
     *
     * @return createtime - 创建时间
     */
    public Date getCreatetime() {
        return createtime;
    }

    /**
     * 设置创建时间
     *
     * @param createtime 创建时间
     */
    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    /**
     * 获取更新时间
     *
     * @return updatetime - 更新时间
     */
    public Date getUpdatetime() {
        return updatetime;
    }

    /**
     * 设置更新时间
     *
     * @param updatetime 更新时间
     */
    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    /**
     * 获取在线时长
     *
     * @return onlinetimelong - 在线时长
     */
    public Long getOnlinetimelong() {
        return onlinetimelong;
    }

    /**
     * 设置在线时长
     *
     * @param onlinetimelong 在线时长
     */
    public void setOnlinetimelong(long onlinetimelong) {
        this.onlinetimelong = onlinetimelong;
    }

    public String getHeadurl() {
		return headurl;
	}

	public void setHeadurl(String headurl) {
		this.headurl = headurl;
	}

	public String getServeruserid() {
		return serveruserid;
	}

	public void setServeruserid(String serveruserid) {
		this.serveruserid = serveruserid;
	}

	/**
     * 获取备用字段2
     *
     * @return standby2 - 备用字段2
     */
    public String getStandby2() {
        return standby2;
    }
    
    
    public String getIsheader() {
		return isheader;
	}

	public void setIsheader(String isheader) {
		this.isheader = isheader;
	}

	/**
     * 设置备用字段2
     *
     * @param standby2 备用字段2
     */
    public void setStandby2(String standby2) {
        this.standby2 = standby2;
    }

	@Override
	public String toString() {
		return "ShopCustPo [id=" + id + ", custid=" + custid + ", custname=" + custname + ", shopid=" + shopid
				+ ", custserverstatus=" + custserverstatus + ", maxusernum=" + maxusernum + ", joinusernum="
				+ joinusernum + ", createtime=" + createtime + ", updatetime=" + updatetime + ", onlinetimelong="
				+ onlinetimelong + ", serveruserid=" + serveruserid + ", headurl=" + headurl + ", standby2=" + standby2
				+ ", isheader=" + isheader + "]";
	}



}