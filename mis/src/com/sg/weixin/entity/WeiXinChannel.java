package com.sg.weixin.entity;

import java.util.Date;

public class WeiXinChannel {
	public WeiXinChannel(){
		
	}
	
	private String id;
	private String channelCodeName;
	private String description;
	private String channelHasChildren;
	private String channelLevelCode;
	private String channelParentCode;
	private int displaySort;
	private String type;
	private String typeValue;
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
	public String getChannelCodeName() {
		return channelCodeName;
	}
	public void setChannelCodeName(String channelCodeName) {
		this.channelCodeName = channelCodeName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getChannelHasChildren() {
		return channelHasChildren;
	}
	public void setChannelHasChildren(String channelHasChildren) {
		this.channelHasChildren = channelHasChildren;
	}
	public String getChannelLevelCode() {
		return channelLevelCode;
	}
	public void setChannelLevelCode(String channelLevelCode) {
		this.channelLevelCode = channelLevelCode;
	}
	public String getChannelParentCode() {
		return channelParentCode;
	}
	public void setChannelParentCode(String channelParentCode) {
		this.channelParentCode = channelParentCode;
	}
	public int getDisplaySort() {
		return displaySort;
	}
	public void setDisplaySort(int displaySort) {
		this.displaySort = displaySort;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTypeValue() {
		return typeValue;
	}
	public void setTypeValue(String typeValue) {
		this.typeValue = typeValue;
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
