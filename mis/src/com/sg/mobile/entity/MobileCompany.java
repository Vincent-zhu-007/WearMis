package com.sg.mobile.entity;

import java.util.Date;

import com.sg.entity.Code;

public class MobileCompany extends Code {
	public MobileCompany(){
		
	}
	
	private String companyId;
	private String keyWord;
	private String mobileHost;
	private String mobilePort;
	private String mobileAppName;
	private String rpcHost;
	private String rpcPort;
	private String rpcAppName;
	private String unzipServerIp1;
	private String unzipServerIp2;
	private int orgStructureLayerNum;
	private String appID;
	private String appSecret;
	private String accessToken;
	private Date tokenCreateTime;
	
	public String getCompanyId() {
		return companyId;
	}
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}
	public String getKeyWord() {
		return keyWord;
	}
	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}
	public String getMobileHost() {
		return mobileHost;
	}
	public void setMobileHost(String mobileHost) {
		this.mobileHost = mobileHost;
	}
	public String getMobilePort() {
		return mobilePort;
	}
	public void setMobilePort(String mobilePort) {
		this.mobilePort = mobilePort;
	}
	public String getMobileAppName() {
		return mobileAppName;
	}
	public void setMobileAppName(String mobileAppName) {
		this.mobileAppName = mobileAppName;
	}
	public String getRpcHost() {
		return rpcHost;
	}
	public void setRpcHost(String rpcHost) {
		this.rpcHost = rpcHost;
	}
	public String getRpcPort() {
		return rpcPort;
	}
	public void setRpcPort(String rpcPort) {
		this.rpcPort = rpcPort;
	}
	public String getRpcAppName() {
		return rpcAppName;
	}
	public void setRpcAppName(String rpcAppName) {
		this.rpcAppName = rpcAppName;
	}
	public String getUnzipServerIp1() {
		return unzipServerIp1;
	}
	public void setUnzipServerIp1(String unzipServerIp1) {
		this.unzipServerIp1 = unzipServerIp1;
	}
	public String getUnzipServerIp2() {
		return unzipServerIp2;
	}
	public void setUnzipServerIp2(String unzipServerIp2) {
		this.unzipServerIp2 = unzipServerIp2;
	}
	public int getOrgStructureLayerNum() {
		return orgStructureLayerNum;
	}
	public void setOrgStructureLayerNum(int orgStructureLayerNum) {
		this.orgStructureLayerNum = orgStructureLayerNum;
	}
	public String getAppID() {
		return appID;
	}
	public void setAppID(String appID) {
		this.appID = appID;
	}
	public String getAppSecret() {
		return appSecret;
	}
	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public Date getTokenCreateTime() {
		return tokenCreateTime;
	}
	public void setTokenCreateTime(Date tokenCreateTime) {
		this.tokenCreateTime = tokenCreateTime;
	}
}
