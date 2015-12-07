package com.sg.entity;

import java.util.Date;

public class CacheConfig {
	public CacheConfig(){
		
	}
	
	private String id;
	private String codeName;
	private String description;
	private String clearUrl;
	private String asyncEtag;
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
	public String getCodeName() {
		return codeName;
	}
	public void setCodeName(String codeName) {
		this.codeName = codeName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getClearUrl() {
		return clearUrl;
	}
	public void setClearUrl(String clearUrl) {
		this.clearUrl = clearUrl;
	}
	public String getAsyncEtag() {
		return asyncEtag;
	}
	public void setAsyncEtag(String asyncEtag) {
		this.asyncEtag = asyncEtag;
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
