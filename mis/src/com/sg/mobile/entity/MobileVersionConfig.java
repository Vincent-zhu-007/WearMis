package com.sg.mobile.entity;

import java.util.Date;

public class MobileVersionConfig {
	public MobileVersionConfig(){
		
	}
	
	private String id;
	private String name;
	private String verFileAddress;
	private String verNo;
	private String verCategory;
	private String verCategorySearch;
	private String verSort;
	private String isMandatoryUpdate;
	private String verRemark;
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getVerFileAddress() {
		return verFileAddress;
	}
	public void setVerFileAddress(String verFileAddress) {
		this.verFileAddress = verFileAddress;
	}
	public String getVerNo() {
		return verNo;
	}
	public void setVerNo(String verNo) {
		this.verNo = verNo;
	}
	public String getVerCategory() {
		return verCategory;
	}
	public void setVerCategory(String verCategory) {
		this.verCategory = verCategory;
	}
	public String getVerCategorySearch() {
		return verCategorySearch;
	}
	public void setVerCategorySearch(String verCategorySearch) {
		this.verCategorySearch = verCategorySearch;
	}
	public String getVerSort() {
		return verSort;
	}
	public void setVerSort(String verSort) {
		this.verSort = verSort;
	}
	public String getIsMandatoryUpdate() {
		return isMandatoryUpdate;
	}
	public void setIsMandatoryUpdate(String isMandatoryUpdate) {
		this.isMandatoryUpdate = isMandatoryUpdate;
	}
	public String getVerRemark() {
		return verRemark;
	}
	public void setVerRemark(String verRemark) {
		this.verRemark = verRemark;
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
