package com.weikefu.transfer.po;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "kefu_message")
public class Message implements Serializable {
    /**
     * 消息id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageid;
    
    /**
     * room id
     */
    private String roomid;

    /**
     * 商户id
     */
    private Integer shopid;
    
    private Integer custid;
    

    /**
     * 用户id
     */
    private String userid;

    /**
     * 用户名称
     */
    private String username;

    /**
     * 客服名称
     */
    private String custname;

    
    /**
     * 商店的名字
     */
    
    private String shopname;
    
    /**
     * 消息的数量
     */
    @Transient
    private long msgcount;
    /**
     * 创建时间
     */
    private Date createtime ;
    
    /**
     * 消息类型: text-文本类型消息；image-图片类型消息；voice-语音类型消息;
     */
    private String msgtype;

    /**
     * 接入方式；1-weixin微信；2-phone手机
     */
    private String joinway;

    /**
     * 发送方式；1-userway用户发送；2-custway客服发送（用于聊天界面左右消息显示），3 shopWay商户发送的就是在商户排队的时候
     */
    private String sendway;

    /**
     * 备用字段1
     */
    private String headurl;

    /**
     * 备用字段2
     */
    private String standby2;

    /**
     * 消息
     */
    private String message;
    private String product_name;
    //商品的图片地址， 商品的url是Message中的地址
    private String product_imgurl;
    private String product_price;

    
    
    private static final long serialVersionUID = 1L;
    
	public String getProduct_name() {
		return product_name;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}

	

	public String getProduct_imgurl() {
		return product_imgurl;
	}

	public void setProduct_imgurl(String product_imgurl) {
		this.product_imgurl = product_imgurl;
	}

	public String getProduct_price() {
		return product_price;
	}

	public void setProduct_price(String product_price) {
		this.product_price = product_price;
	}

	public long getMsgcount() {
		return msgcount;
	}

	public void setMsgcount(long msgcount) {
		this.msgcount = msgcount;
	}

	public String getRoomid() {
		return roomid;
	}

	public void setRoomid(String roomid) {
		this.roomid = roomid;
	}

	public String getShopname() {
		return shopname;
	}

	public void setShopname(String shopname) {
		this.shopname = shopname;
	}

	public Integer getCustid() {
		return custid;
	}

	public void setCustid(Integer custid) {
		this.custid = custid;
	}


	

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
     * 获取用户id
     *
     * @return userid - 用户id
     */
    public String getUserid() {
        return userid;
    }

    /**
     * 设置用户id
     *
     * @param userid 用户id
     */
    public void setUserid(String userid) {
        this.userid = userid;
    }

    /**
     * 获取用户名称
     *
     * @return username - 用户名称
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置用户名称
     *
     * @param username 用户名称
     */
    public void setUsername(String username) {
        this.username = username;
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
     * 获取消息类型: text-文本类型消息；image-图片类型消息；voice-语音类型消息;
     *
     * @return msgtype - 消息类型: text-文本类型消息；image-图片类型消息；voice-语音类型消息;
     */
    public String getMsgtype() {
        return msgtype;
    }

    /**
     * 设置消息类型: text-文本类型消息；image-图片类型消息；voice-语音类型消息;
     *
     * @param msgtype 消息类型: text-文本类型消息；image-图片类型消息；voice-语音类型消息;
     */
    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    /**
     * 获取接入方式；1-weixin微信；2-phone手机
     *
     * @return joinway - 接入方式；1-weixin微信；2-phone手机
     */
    public String getJoinway() {
        return joinway;
    }

    /**
     * 设置接入方式；1-weixin微信；2-phone手机
     *
     * @param joinway 接入方式；1-weixin微信；2-phone手机
     */
    public void setJoinway(String joinway) {
        this.joinway = joinway;
    }

    /**
     * 获取发送方式；1-userway用户发送；2-custway客服发送（用于聊天界面左右消息显示）
     *
     * @return sendway - 发送方式；1-userway用户发送；2-custway客服发送（用于聊天界面左右消息显示）
     */
    public String getSendway() {
        return sendway;
    }

    /**
     * 设置发送方式；1-userway用户发送；2-custway客服发送（用于聊天界面左右消息显示）
     *
     * @param sendway 发送方式；1-userway用户发送；2-custway客服发送（用于聊天界面左右消息显示）
     */
    public void setSendway(String sendway) {
        this.sendway = sendway;
    }

    
    public String getHeadurl() {
		return headurl;
	}

	public void setHeadurl(String headurl) {
		this.headurl = headurl;
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

    /**
     * 获取消息
     *
     * @return message - 消息
     */
    public String getMessage() {
        return message;
    }

    /**
     * 设置消息
     *
     * @param message 消息
     */
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", messageid=").append(messageid);
        sb.append(", shopid=").append(shopid);
        sb.append(",roomid=").append(roomid);
        sb.append(", custid=").append(custid);
        sb.append(", userid=").append(userid);
        sb.append(", username=").append(username);
        sb.append(", custname=").append(custname);
        sb.append(", createtime=").append(createtime);
        sb.append(", msgtype=").append(msgtype);
        sb.append(", joinway=").append(joinway);
        sb.append(", sendway=").append(sendway);
        sb.append(", headurl=").append(headurl);
        sb.append(", standby2=").append(standby2);
        sb.append(", message=").append(message);
        sb.append("]");
        return sb.toString();
    }
}