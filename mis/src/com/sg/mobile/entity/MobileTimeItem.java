package com.sg.mobile.entity;

public class MobileTimeItem {
	public MobileTimeItem(){
		
	}
	
	private String id;
	private String ownerUri;
	private String startTime;
	private String endTime;
	private String level;
	private String listFileName;
	
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
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getListFileName() {
		return listFileName;
	}
	public void setListFileName(String listFileName) {
		this.listFileName = listFileName;
	}
}
