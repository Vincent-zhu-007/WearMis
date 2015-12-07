package com.sg.mobile.entity;

import java.util.List;

public class WebSiteMenu {
	public WebSiteMenu(){
		
	}
	
	private String menuId;
	private String menuName;
	private String url;
	private List<WebSiteMenu> childWebSiteMenu;
	
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
	public List<WebSiteMenu> getChildWebSiteMenu() {
		return childWebSiteMenu;
	}
	public void setChildWebSiteMenu(List<WebSiteMenu> childWebSiteMenu) {
		this.childWebSiteMenu = childWebSiteMenu;
	}
}
