package com.sg.mobile.entity;

public class MobileUserFileShare {
	public MobileUserFileShare(){
		
	}
	
	private String id;
	private String mobileUserFileId;
	private String targetMemberUri;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMobileUserFileId() {
		return mobileUserFileId;
	}
	public void setMobileUserFileId(String mobileUserFileId) {
		this.mobileUserFileId = mobileUserFileId;
	}
	public String getTargetMemberUri() {
		return targetMemberUri;
	}
	public void setTargetMemberUri(String targetMemberUri) {
		this.targetMemberUri = targetMemberUri;
	}
}
