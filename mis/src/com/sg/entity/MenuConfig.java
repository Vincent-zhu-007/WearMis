package com.sg.entity;

public class MenuConfig {
	public MenuConfig() {
		
	}
	
	private String id;
	private String code;
	private String description;
	private String url;
	private String hasChildren;
	private Integer displaySort;
	private String levelCode;
	private String parentCode;
	private String status;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getHasChildren() {
		return hasChildren;
	}
	public void setHasChildren(String hasChildren) {
		this.hasChildren = hasChildren;
	}
	public Integer getDisplaySort() {
		return displaySort;
	}
	public void setDisplaySort(Integer displaySort) {
		this.displaySort = displaySort;
	}
	public String getLevelCode() {
		return levelCode;
	}
	public void setLevelCode(String levelCode) {
		this.levelCode = levelCode;
	}
	public String getParentCode() {
		return parentCode;
	}
	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
