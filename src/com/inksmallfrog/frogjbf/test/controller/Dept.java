package com.inksmallfrog.frogjbf.test.controller;

import javax.servlet.http.HttpServletRequest;

import com.inksmallfrog.frogjbf.annotation.AutoInject;
import com.inksmallfrog.frogjbf.annotation.Param;
import com.inksmallfrog.frogjbf.global.JBFContext;
import com.inksmallfrog.frogjbf.test.entity.DeptBean;
import com.inksmallfrog.frogjbf.test.service.DeptService;

public class Dept {
	@AutoInject
	private HttpServletRequest request;
	@Param
	private String title;
	
	public String queryDept(){
		DeptService service = (DeptService) JBFContext.getAppContext().getService("deptService");
		request.setAttribute("depts", service.getAllDepts());
		return "newDept.jsp";
	}
	public String insertDept(){
		DeptService service = (DeptService) JBFContext.getAppContext().getService("deptService");
		request.setAttribute("id", service.createDept(new DeptBean(title)));
		request.setAttribute("depts", service.getAllDepts());
		return "newDept.jsp";
	}
}
