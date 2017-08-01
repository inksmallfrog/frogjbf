package com.inksmallfrog.frogjbf.test.entity;

import com.inksmallfrog.frogjbf.annotation.Column;
import com.inksmallfrog.frogjbf.annotation.TableName;

@TableName(name="DEPT")
public class DeptBean {
	@Column(name="ID")
	private int id;
	@Column(name="TITLE")
	private String title;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	@Override
	public String toString() {
		return "DeptBean [id=" + id + ", title=" + title + "]";
	}
	public DeptBean(int id, String title) {
		super();
		this.id = id;
		this.title = title;
	}
	public DeptBean(String title) {
		super();
		this.title = title;
	}
	public DeptBean() {
		super();
	}
}
