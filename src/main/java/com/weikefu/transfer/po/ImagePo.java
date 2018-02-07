package com.weikefu.transfer.po;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
@Entity
@Table(name ="kefu_image")
public class ImagePo implements Serializable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long imgid;
	
	private String url;
	
	private String size;
	
	private String title;
	
	private String custid;
	private String shopid;
	private String userid;
	
	private Date createtime;


	
	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public Long getImgid() {
		return imgid;
	}

	public void setImgid(Long imgid) {
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", imgid=").append(imgid);
        sb.append(", url=").append(url);
        sb.append(", shopid=").append(shopid);
        sb.append(", userid=").append(custid);
        sb.append(", title=").append(title);
        sb.append(", size=").append(size);
        sb.append(", createtime=").append(createtime);
        sb.append("]");
        return sb.toString();
    }
	
}
