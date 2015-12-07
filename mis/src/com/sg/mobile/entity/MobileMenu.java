package com.sg.mobile.entity;

import java.util.List;

public class MobileMenu {
	public MobileMenu() {
		
	}
	
	private String menuId;
	private String menuName;
	private String url;
	private List<MobileMenu> childMobileMenu;
	
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
	public List<MobileMenu> getChildMobileMenu() {
		return childMobileMenu;
	}
	public void setChildMobileMenu(List<MobileMenu> childMobileMenu) {
		this.childMobileMenu = childMobileMenu;
	}
}
