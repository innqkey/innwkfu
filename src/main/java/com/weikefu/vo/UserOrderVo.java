package com.weikefu.vo;

/** 
* @author qinkai 
* @date 2018年1月3日
*/

public class UserOrderVo {
	//订单图片
	String orderImg;
	//订单名称
	String orderName;
	//订单号
	String orderNumber;
	//付款时间 
	String payTime;
	//下单时间 
	String orderTime;
	//实付价格
	String price;
	//付款方式
	String payType;
	//购买数量
	Integer buyNum;
	//订单id
	String orderId;
	
	
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public Integer getBuyNum() {
		return buyNum;
	}
	public void setBuyNum(Integer buyNum) {
		this.buyNum = buyNum;
	}
	public String getOrderImg() {
		return orderImg;
	}
	public void setOrderImg(String orderImg) {
		this.orderImg = orderImg;
	}
	public String getOrderName() {
		return orderName;
	}
	public void setOrderName(String orderName) {
		this.orderName = orderName;
	}
	public String getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	public String getPayTime() {
		return payTime;
	}
	public void setPayTime(String payTime) {
		this.payTime = payTime;
	}
	public String getOrderTime() {
		return orderTime;
	}
	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
	
}
