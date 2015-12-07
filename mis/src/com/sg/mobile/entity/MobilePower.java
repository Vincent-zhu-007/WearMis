package com.sg.mobile.entity;

import java.util.Date;

public class MobilePower {
	public MobilePower(){
		
	}
	
	private String id;
	private String ownerUri;
	private String percent;
	private String status;
	private Date createTime;
	private String creator;
	private Date updateTime;
	private String updator;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOwnerUri() {
		return ownerUri;
	}
	public void setOwnerUri(String ownerUri) {
		this.ownerUri = ownerUri;
	}
	public String getPercent() {
		return percent;
	}
	public void setPercent(String percent) {
		this.percent = percent;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public String getUpdator() {
		return updator;
	}
	public void setUpdator(String updator) {
		this.updator = updator;
	}
}
