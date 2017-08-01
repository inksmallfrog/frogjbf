package com.inksmallfrog.frogjbf.test.controller;

import javax.servlet.http.HttpServletRequest;

import com.inksmallfrog.frogjbf.annotation.AutoInject;
import com.inksmallfrog.frogjbf.annotation.Param;
import com.inksmallfrog.frogjbf.datasource.PageInfo;
import com.inksmallfrog.frogjbf.global.JBFContext;
import com.inksmallfrog.frogjbf.test.entity.DeptBean;
import com.inksmallfrog.frogjbf.test.service.DeptService;

public class Dept {
	@AutoInject
	private HttpServletRequest request;
	@Param
	private String title;
	@Param
	private String page;
	
	final int PAGE_SIZE = 3;
	
	public String queryDept(){
		int pageNum = 1;
		if(page != null){
			pageNum = Integer.parseInt(page);
		}
		PageInfo pageInfo = new PageInfo(pageNum, PAGE_SIZE);
		DeptService service = (DeptService) JBFContext.getAppContext().getService("deptService");
		request.setAttribute("depts", service.getDeptsAtPage(pageInfo));
		request.setAttribute("pageInfo", pageInfo);
		return "newDept.jsp";
	}
	public String insertDept(){
		DeptService service = (DeptService) JBFContext.getAppContext().getService("deptService");
		request.setAttribute("id", service.createDept(new DeptBean(title)));
		request.setAttribute("depts", service.getAllDepts());
		return "newDept.jsp";
	}
}
