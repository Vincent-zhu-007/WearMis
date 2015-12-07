package com.sg.mobile.entity;

public class MobileRpcParamenter {
	public MobileRpcParamenter(){
		
	}
	
	private String paraName;
	private String paraType;
	private String paraValue;
	private int paraSort;
	public String getParaName() {
		return paraName;
	}
	public void setParaName(String paraName) {
		this.paraName = paraName;
	}
	public String getParaType() {
		return paraType;
	}
	public void setParaType(String paraType) {
		this.paraType = paraType;
	}
	public String getParaValue() {
		return paraValue;
	}
	public void setParaValue(String paraValue) {
		this.paraValue = paraValue;
	}
	public int getParaSort() {
		return paraSort;
	}
	public void setParaSort(int paraSort) {
		this.paraSort = paraSort;
	}
}
