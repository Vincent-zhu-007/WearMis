package com.sg.mobile.entity;

import com.sg.entity.Code;

public class MobileOrgStructure extends Code {
	public MobileOrgStructure(){
		
	}
	
	private String orgStructureId;
	private String hasChildren;
	private int displaySort;
	private int levelNum;
	private String parentCode;
	
	public String getOrgStructureId() {
		return orgStructureId;
	}
	public void setOrgStructureId(String orgStructureId) {
		this.orgStructureId = orgStructureId;
	}
	public String getHasChildren() {
		return hasChildren;
	}
	public void setHasChildren(String hasChildren) {
		this.hasChildren = hasChildren;
	}
	public int getDisplaySort() {
		return displaySort;
	}
	public void setDisplaySort(int displaySort) {
		this.displaySort = displaySort;
	}
	public int getLevelNum() {
		return levelNum;
	}
	public void setLevelNum(int levelNum) {
		this.levelNum = levelNum;
	}
	public String getParentCode() {
		return parentCode;
	}
	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}
}
