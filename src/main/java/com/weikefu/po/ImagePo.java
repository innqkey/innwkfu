package com.weikefu.po;

import java.io.Serializable;
import java.util.Date;



import org.springframework.data.mongodb.core.mapping.Document;

import com.weikefu.annotation.AutoIncKey;

//@Table(name ="kefu_image")
@Document(collection="kefu_image")
public class ImagePo implements Serializable{
	
	//@Id
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
	@org.springframework.data.annotation.Id
	private String imgid;
	
	private String url;
	
	private String size;
	
	private String title;
	
	private String custid;
	private String shopid;
	private String userid;
	private String weixin_Media_id;
	
	private Date createtime;


	
	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	
	public String getImgid() {
		return imgid;
	}

	public void setImgid(String imgid) {
		this.imgid = imgid;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCustid() {
		return custid;
	}

	public void setCustid(String custid) {
		this.custid = custid;
	}

	public String getShopid() {
		return shopid;
	}

	public void setShopid(String shopid) {
		this.shopid = shopid;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	
	public String getWeixin_Media_id() {
		return weixin_Media_id;
	}

	public void setWeixin_Media_id(String weixin_Media_id) {
		this.weixin_Media_id = weixin_Media_id;
	}

	@Override
	public String toString() {
		return "ImagePo [imgid=" + imgid + ", url=" + url + ", size=" + size + ", title=" + title + ", custid=" + custid
				+ ", shopid=" + shopid + ", userid=" + userid + ", weixin_Media_id=" + weixin_Media_id + ", createtime="
				+ createtime + "]";
	}



 
}
