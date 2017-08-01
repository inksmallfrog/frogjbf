package com.inksmallfrog.frogjbf.datasource;

public class PageInfo {
	private int totalPage;
	private int pageSize;
	private int rowCount;
	private int curPage;
	
	public PageInfo(int curPage, int pageSize) {
		super();
		this.pageSize = pageSize;
		this.curPage = curPage;
	}
	
	public int getCurPage() {
		return curPage;
	}
	public void setCurPage(int curPage) {
		this.curPage = curPage;
	}
	public int getTotalPage() {
		return totalPage;
	}
	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getRowCount() {
		return rowCount;
	}
	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}
	
}
