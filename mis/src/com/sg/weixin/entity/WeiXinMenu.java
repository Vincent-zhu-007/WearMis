package com.sg.weixin.entity;

import java.util.List;

public class WeiXinMenu {
	public WeiXinMenu(){
		
	}
	
	private String menuId;
	private String menuName;
	private String url;
	private List<WeiXinMenu> childWeiXinMenu;
	
	public String getMenuId() {
		return menuId;
	}
	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}
	public String getMenuName() {
		return menuName;
	}
	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public List<WeiXinMenu> getChildWeiXinMenu() {
		return childWeiXinMenu;
	}
	public void setChildWeiXinMenu(List<WeiXinMenu> childWeiXinMenu) {
		this.childWeiXinMenu = childWeiXinMenu;
	}
}
