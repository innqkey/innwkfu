package com.weikefu.po;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import org.springframework.data.mongodb.core.mapping.Document;

import com.weikefu.annotation.AutoIncKey;

//@Table(name = "kefu_quick_message")
@Document(collection = "kefu_quick_message")
public class QuickMessagePo implements Serializable {
    /**
     * 快捷回复id
     */
    @AutoIncKey
    @org.springframework.data.annotation.Id
    private Long messageid =0L;

    /**
     * 商户id
     */
    private Integer shopid;

    /**
     * 客服id
     */
    private Integer custid;

    /**
     * 快捷回复消息
     */
    private String message;
    
    /**
     * 快捷回复是否删除：1--正常，2--删除
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createtime;

    /**
     * 备用字段1
     */
    private Date updatetime;

    /**
     * 备用字段2
     */
    private String standby2;

    private static final long serialVersionUID = 1L;

   
    public Long getMessageid() {
		return messageid;
	}

	public void setMessageid(Long messageid) {
		this.messageid = messageid;
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
     * 获取快捷回复消息
     *
     * @return message - 快捷回复消息
     */
    public String getMessage() {
        return message;
    }

    /**
     * 设置快捷回复消息
     *
     * @param message 快捷回复消息
     */
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
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

	public Date getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}

	/**
     * 获取备用字段2
     *
     * @return standby2 - 备用字段2
     */
    public String getStandby2() {
        return standby2;
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
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", messageid=").append(messageid);
        sb.append(", shopid=").append(shopid);
        sb.append(", custid=").append(custid);
        sb.append(", message=").append(message);
        sb.append(", status=").append(status);
        sb.append(", createtime=").append(createtime);
        sb.append(", updatetime=").append(updatetime);
        sb.append(", standby2=").append(standby2);
        sb.append("]");
        return sb.toString();
    }
}