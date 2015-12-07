package com.sg.mobile.entity;

public class MobileContactMember {
	public MobileContactMember(){
		
	}
	
	private String id;
	private String ownerUri;
	private String displayName;
	private String shortNum;
	private String mobilePhone;
	private String numButton;
	private String listFileName;
	
	public String getOwnerUri() {
		return ownerUri;
	}
	public void setOwnerUri(String ownerUri) {
		this.ownerUri = ownerUri;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getShortNum() {
		return shortNum;
	}
	public void setShortNum(String shortNum) {
		this.shortNum = shortNum;
	}
	public String getMobilePhone() {
		return mobilePhone;
	}
	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}
	public String getNumButton() {
		return numButton;
	}
	public void setNumButton(String numButton) {
		this.numButton = numButton;
	}
	public String getListFileName() {
		return listFileName;
	}
	public void setListFileName(String listFileName) {
		this.listFileName = listFileName;
	}
}
