package com.sg.util;

public class DataGridModel implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4594159176352119094L;
	
	private int page;
	private int rows;

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public int getStartRow() {
		return (page - 1) * rows;
	}
}
