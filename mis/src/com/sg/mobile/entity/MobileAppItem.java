package com.sg.mobile.entity;

public class MobileAppItem {
	public MobileAppItem(){
		
	}
	
	private String id;
	private String ownerUri;
	private String appCodeName;
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
	public String getAppCodeName() {
		return appCodeName;
	}
	public void setAppCodeName(String appCodeName) {
		this.appCodeName = appCodeName;
	}
	public String getListFileName() {
		return listFileName;
	}
	public void setListFileName(String listFileName) {
		this.listFileName = listFileName;
	}
}
