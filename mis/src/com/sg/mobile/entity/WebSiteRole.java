package com.sg.mobile.entity;

import com.sg.entity.Code;

public class WebSiteRole extends Code {
	public WebSiteRole(){
		
	}
	
	private String roleId;
	private String permission;
	
	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	public String getPermission() {
		return permission;
	}
	public void setPermission(String permission) {
		this.permission = permission;
	}
}
